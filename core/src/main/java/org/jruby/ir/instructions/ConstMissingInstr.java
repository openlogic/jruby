package org.jruby.ir.instructions;

import org.jcodings.specific.USASCIIEncoding;
import org.jruby.RubyModule;
import org.jruby.ir.IRVisitor;
import org.jruby.ir.Operation;
import org.jruby.ir.operands.Operand;
import org.jruby.ir.operands.Symbol;
import org.jruby.ir.operands.Variable;
import org.jruby.ir.transformations.inlining.CloneInfo;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.CallType;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class ConstMissingInstr extends CallInstr implements FixedArityInstr {
    private final String missingConst;

    public ConstMissingInstr(Variable result, Operand currentModule, String missingConst) {
        // FIXME: Missing encoding knowledge of the constant name.
        super(Operation.CONST_MISSING, CallType.FUNCTIONAL, result, "const_missing", currentModule, new Operand[]{new Symbol(missingConst, USASCIIEncoding.INSTANCE)}, null);

        this.missingConst = missingConst;
    }

    public String getMissingConst() {
        return missingConst;
    }

    @Override
    public Instr clone(CloneInfo ii) {
        return new ConstMissingInstr(ii.getRenamedVariable(result), getReceiver().cloneForInlining(ii), missingConst);
    }

    @Override
    public String toString() {
        return getOperation() + "(" + getReceiver() + "," + missingConst  + ")";
    }

    @Override
    public Object interpret(ThreadContext context, StaticScope currScope, DynamicScope currDynScope, IRubyObject self, Object[] temp) {
        RubyModule module = (RubyModule) getReceiver().retrieve(context, self, currScope, currDynScope, temp);
        return module.callMethod(context, "const_missing", context.runtime.fastNewSymbol(missingConst));
    }

    @Override
    public void visit(IRVisitor visitor) {
        visitor.ConstMissingInstr(this);
    }
}
