/*
 *  Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.  Oracle designates this
 *  particular file as subject to the "Classpath" exception as provided
 *  by Oracle in the LICENSE file that accompanied this code.
 *
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 *
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *   Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 *  or visit www.oracle.com if you need additional information or have any
 *  questions.
 *
 */
package jdk.internal.clang;

import jdk.incubator.foreign.Addressable;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.SegmentAllocator;
import jdk.internal.clang.libclang.Index_h;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static jdk.internal.clang.libclang.Index_h.C_INT;
import static jdk.internal.clang.libclang.Index_h.C_POINTER;

public class SourceLocation {

    private final MemorySegment loc;

    SourceLocation(MemorySegment loc) {
        this.loc = loc;
    }

    @FunctionalInterface
    private interface LocationFactory {
        void get(MemorySegment loc, Addressable file,
                 Addressable line, Addressable column, Addressable offset);
    }

    @SuppressWarnings("unchecked")
    private Location getLocation(LocationFactory fn) {
        try (var scope = ResourceScope.newConfinedScope()) {
             MemorySegment file = MemorySegment.allocateNative(C_POINTER, scope);
             MemorySegment line = MemorySegment.allocateNative(C_INT, scope);
             MemorySegment col = MemorySegment.allocateNative(C_INT, scope);
             MemorySegment offset = MemorySegment.allocateNative(C_INT, scope);

            fn.get(loc, file, line, col, offset);
            MemoryAddress fname = file.get(C_POINTER, 0);
            String str = fname == MemoryAddress.NULL ?  null : getFileName(fname);

            return new Location(str, line.get(C_INT, 0),
                col.get(C_INT, 0), offset.get(C_INT, 0));
        }
    }

    private static String getFileName(MemoryAddress fname) {
        return LibClang.CXStrToString(allocator ->
                Index_h.clang_getFileName(allocator, fname));
    }

    public Location getFileLocation() { return getLocation(Index_h::clang_getFileLocation); }
    public Location getExpansionLocation() { return getLocation(Index_h::clang_getExpansionLocation); }
    public Location getSpellingLocation() { return getLocation(Index_h::clang_getSpellingLocation); }
    public boolean isInSystemHeader() {
        return Index_h.clang_Location_isInSystemHeader(loc) != 0;
    }

    public boolean isFromMainFile() {
        return Index_h.clang_Location_isFromMainFile(loc) != 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SourceLocation)) {
            return false;
        }
        SourceLocation sloc = (SourceLocation)other;
        return Objects.equals(getFileLocation(), sloc.getFileLocation());
    }

    @Override
    public int hashCode() {
        return getFileLocation().hashCode();
    }

    public final static class Location {
        private final Path path;
        private final int line;
        private final int column;
        private final int offset;

        private Location(String filename, int line, int column, int offset) {
            if (filename == null || filename.isEmpty()) {
                this.path = null;
            } else {
                this.path = Paths.get(filename);
            }

            this.line = line;
            this.column = column;
            this.offset = offset;
        }

        public Path path() {
            return path;
        }

        public int line() {
            return line;
        }

        public int column() {
            return column;
        }

        public int offset() {
            return offset;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Location)) {
                return false;
            }
            Location loc = (Location)other;
            return Objects.equals(path, loc.path) &&
                line == loc.line && column == loc.column &&
                offset == loc.offset;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(path) ^ line ^ column ^ offset;
        }

        @Override
        public String toString() {
            return Objects.toString(path) + ":" + line + ":" + column + ":" + offset;
        }
    }
}
