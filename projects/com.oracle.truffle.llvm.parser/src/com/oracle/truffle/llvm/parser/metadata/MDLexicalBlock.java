/*
 * Copyright (c) 2016, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.truffle.llvm.parser.metadata;

import com.oracle.truffle.llvm.parser.listeners.Metadata;

public final class MDLexicalBlock implements MDBaseNode {

    private final long line;
    private final long column;

    private MDBaseNode scope;
    private MDBaseNode file;

    private MDLexicalBlock(long line, long column) {
        this.line = line;
        this.column = column;

        this.scope = MDVoidNode.INSTANCE;
        this.file = MDVoidNode.INSTANCE;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    public MDBaseNode getFile() {
        return file;
    }

    public long getLine() {
        return line;
    }

    public long getColumn() {
        return column;
    }

    public MDBaseNode getScope() {
        return scope;
    }

    @Override
    public void replace(MDBaseNode oldValue, MDBaseNode newValue) {
        if (scope == oldValue) {
            scope = newValue;
        }
        if (file == oldValue) {
            file = newValue;
        }
    }

    private static final int ARGINDEX_38_SCOPE = 1;
    private static final int ARGINDEX_38_FILE = 2;
    private static final int ARGINDEX_38_LINE = 3;
    private static final int ARGINDEX_38_COLUMN = 4;

    public static MDLexicalBlock create38(long[] args, MetadataValueList md) {
        // [distinct, scope, file, line, column]
        final long line = args[ARGINDEX_38_LINE];
        final long column = args[ARGINDEX_38_COLUMN];

        final MDLexicalBlock block = new MDLexicalBlock(line, column);
        block.scope = md.getNullable(args[ARGINDEX_38_SCOPE], block);
        block.file = md.getNullable(args[ARGINDEX_38_FILE], block);
        return block;
    }

    private static final int ARGINDEX_32_SCOPE = 1;
    private static final int ARGINDEX_32_LINE = 2;
    private static final int ARGINDEX_32_COLUMN = 3;
    private static final int ARGINDEX_32_FILE = 4;

    public static MDLexicalBlock create32(long[] args, Metadata md) {
        final long line = ParseUtil.asInt(args, ARGINDEX_32_LINE, md);
        final long column = ParseUtil.asInt(args, ARGINDEX_32_COLUMN, md);
        // asInt32(args[5); // Unique ID to identify blocks from a template function

        final MDLexicalBlock block = new MDLexicalBlock(line, column);
        block.scope = ParseUtil.resolveReference(args, ARGINDEX_32_SCOPE, block, md);
        block.file = ParseUtil.resolveReference(args, ARGINDEX_32_FILE, block, md);
        return block;
    }
}
