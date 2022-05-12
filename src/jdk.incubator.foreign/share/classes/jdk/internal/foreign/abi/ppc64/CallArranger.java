/*
 * Copyright (c) 2020, 2021, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019, 2021, Arm Limited. All rights reserved.
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
package jdk.internal.foreign.abi.ppc64;

import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.GroupLayout;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.NativeSymbol;
import jdk.incubator.foreign.ResourceScope;
import jdk.internal.foreign.Utils;
import jdk.internal.foreign.abi.CallingSequenceBuilder;
import jdk.internal.foreign.abi.ABIDescriptor;
import jdk.internal.foreign.abi.Binding;
import jdk.internal.foreign.abi.CallingSequence;
import jdk.internal.foreign.abi.ProgrammableInvoker;
import jdk.internal.foreign.abi.ProgrammableUpcallHandler;
import jdk.internal.foreign.abi.VMStorage;
import jdk.internal.foreign.abi.SharedUtils;
import jdk.internal.foreign.abi.ppc64.aix.AixPPC64CallArranger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static jdk.internal.foreign.PlatformLayouts.*;

/**
 * From AArch64 CallArranger. See that file for more detail on what this file should do...
 *
 * For the AArch64 C ABI specifically, this class uses the ProgrammableInvoker API, namely CallingSequenceBuilder2
 * to translate a C FunctionDescriptor into a CallingSequence2, which can then be turned into a MethodHandle.
 *
 * This includes taking care of synthetic arguments like pointers to return buffers for 'in-memory' returns.
 *
 * There are minor differences between the ABIs implemented on Linux, macOS, and Windows
 * which are handled in sub-classes. Clients should access these through the provided
 * public constants CallArranger.AIX.
 */
public class CallArranger {
    /* Stub */
    public static final CallArranger AIX = new AixPPC64CallArranger();

    public MethodHandle arrangeDowncall(MethodType mt, FunctionDescriptor cDesc) {
        throw new RuntimeException("Unimplemented");
    }

    public NativeSymbol arrangeUpcall(MethodHandle target, MethodType mt, FunctionDescriptor cDesc, ResourceScope scope) {
        throw new RuntimeException("Unimplemented");
    }
}
