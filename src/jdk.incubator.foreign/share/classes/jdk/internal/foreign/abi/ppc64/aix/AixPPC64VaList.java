/*
 * Copyright (c) 2020, 2021, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020, 2021, Arm Limited. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package jdk.internal.foreign.abi.ppc64.aix;

import jdk.incubator.foreign.*;
import jdk.internal.foreign.ResourceScopeImpl;
import jdk.internal.foreign.Scoped;
import jdk.internal.foreign.Utils;
import jdk.internal.foreign.abi.SharedUtils;
import jdk.internal.misc.Unsafe;

import java.lang.invoke.VarHandle;
import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jdk.incubator.foreign.MemoryLayout.PathElement.groupElement;
import static jdk.internal.foreign.abi.SharedUtils.SimpleVaArg;
import static jdk.internal.foreign.abi.SharedUtils.THROWING_ALLOCATOR;

/**
 */
public non-sealed class AixPPC64VaList implements VaList, Scoped {
    private static final Unsafe U = Unsafe.getUnsafe();

    static final Class<?> CARRIER = MemoryAddress.class;

    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
               /* TODO */
    ).withName("__va_list");

    private static final VaList EMPTY
        = new SharedUtils.EmptyVaList(emptyListAddress());

    private final MemorySegment segment;
    private final MemorySegment gpRegsArea;
    private final MemorySegment fpRegsArea;

    private AixPPC64VaList(MemorySegment segment, MemorySegment gpRegsArea, MemorySegment fpRegsArea) {
        this.segment = segment;
        this.gpRegsArea = gpRegsArea;
        this.fpRegsArea = fpRegsArea;
    }

    public static VaList empty() {
        return EMPTY;
    }

    private static MemoryAddress emptyListAddress() {
        throw new RuntimeException("Unimplemented");
    }

    private static AixPPC64VaList readFromSegment(MemorySegment segment) {
        throw new RuntimeException("Unimplemented");
    }

    private void preAlignStack(MemoryLayout layout) {
        throw new RuntimeException("Unimplemented");
    }

    private void postAlignStack(MemoryLayout layout) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public int nextVarg(ValueLayout.OfInt layout) {
        return (int) read(int.class, layout);
    }

    @Override
    public long nextVarg(ValueLayout.OfLong layout) {
        return (long) read(long.class, layout);
    }

    @Override
    public double nextVarg(ValueLayout.OfDouble layout) {
        return (double) read(double.class, layout);
    }

    @Override
    public MemoryAddress nextVarg(ValueLayout.OfAddress layout) {
        return (MemoryAddress) read(MemoryAddress.class, layout);
    }

    @Override
    public MemorySegment nextVarg(GroupLayout layout, SegmentAllocator allocator) {
        Objects.requireNonNull(allocator);
        return (MemorySegment) read(MemorySegment.class, layout, allocator);
    }

    private Object read(Class<?> carrier, MemoryLayout layout) {
        return read(carrier, layout, THROWING_ALLOCATOR);
    }

    private Object read(Class<?> carrier, MemoryLayout layout, SegmentAllocator allocator) {
        Objects.requireNonNull(layout);
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public void skip(MemoryLayout... layouts) {
        Objects.requireNonNull(layouts);
        throw new RuntimeException("Unimplemented");
    }

    static AixPPC64VaList.Builder builder(ResourceScope scope) {
        return new AixPPC64VaList.Builder(scope);
    }

    public static VaList ofAddress(MemoryAddress ma, ResourceScope scope) {
        return readFromSegment(MemorySegment.ofAddress(ma, LAYOUT.byteSize(), scope));
    }

    @Override
    public ResourceScope scope() {
        return segment.scope();
    }

    @Override
    public VaList copy() {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public MemoryAddress address() {
        return segment.address();
    }

    @Override
    public String toString() {
        return "AixPPC64VaList{"
            + '}';
    }

    public static non-sealed class Builder implements VaList.Builder {
        private final ResourceScope scope;
        private final MemorySegment gpRegs;
        private final MemorySegment fpRegs;

        private long currentGPOffset = 0;
        private long currentFPOffset = 0;
        private final List<SimpleVaArg> stackArgs = new ArrayList<>();

        Builder(ResourceScope scope) {
            this.scope = scope;
            // TODO
            this.gpRegs = null;
            this.fpRegs = null;
            // this.gpRegs = MemorySegment.allocateNative(LAYOUT_GP_REGS, scope);
            // this.fpRegs = MemorySegment.allocateNative(LAYOUT_FP_REGS, scope);
        }

        @Override
        public Builder addVarg(ValueLayout.OfInt layout, int value) {
            return arg(int.class, layout, value);
        }

        @Override
        public Builder addVarg(ValueLayout.OfLong layout, long value) {
            return arg(long.class, layout, value);
        }

        @Override
        public Builder addVarg(ValueLayout.OfDouble layout, double value) {
            return arg(double.class, layout, value);
        }

        @Override
        public Builder addVarg(ValueLayout.OfAddress layout, Addressable value) {
            return arg(MemoryAddress.class, layout, value.address());
        }

        @Override
        public Builder addVarg(GroupLayout layout, MemorySegment value) {
            return arg(MemorySegment.class, layout, value);
        }

        private Builder arg(Class<?> carrier, MemoryLayout layout, Object value) {
            Objects.requireNonNull(layout);
            Objects.requireNonNull(value);
            // TODO: Implement
            return this;
        }

        private boolean isEmpty() {
            throw new RuntimeException("Unimplemented");
        }

        public VaList build() {
            if (isEmpty()) {
                return EMPTY;
            }

            throw new RuntimeException("Unimplemented");
        }
    }
}
