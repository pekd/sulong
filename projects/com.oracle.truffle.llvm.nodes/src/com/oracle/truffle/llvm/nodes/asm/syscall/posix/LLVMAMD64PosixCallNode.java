package com.oracle.truffle.llvm.nodes.asm.syscall.posix;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.InteropException;
import com.oracle.truffle.api.interop.Message;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMNode;

public abstract class LLVMAMD64PosixCallNode extends LLVMNode {
    private final String name;
    private final String signature;

    @Child private Node nativeExecute;

    public LLVMAMD64PosixCallNode(String name, String signature, int args) {
        this.name = name;
        this.signature = signature;
        nativeExecute = Message.createExecute(args).createNode();
    }

    protected TruffleObject createFunction() {
        return getContext().getNativeLookup().getNativeFunction("@__sulong_posix_" + name, String.format(signature));
    }

    // Workaround for nice syntax + Truffle DSL
    public final Object execute(Object... args) {
        return executeObject(args);
    }

    public abstract Object executeObject(Object[] args);

    @Specialization
    public Object execute(Object[] args, @Cached("createFunction()") TruffleObject function) {
        try {
            return ForeignAccess.sendExecute(nativeExecute, function, args);
        } catch (InteropException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String toString() {
        return "posix " + name;
    }
}
