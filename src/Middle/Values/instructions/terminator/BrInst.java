package Middle.Values.instructions.terminator;

import Middle.Types.IntegerType;
import Middle.Types.VoidType;
import Middle.Values.BasicBlock;
import Middle.Values.BuildFactory;
import Middle.Values.ConstInt;
import Middle.Values.Value;
import Middle.Values.instructions.Operator;

public class BrInst extends TerminatorInst {
    public BrInst(BasicBlock basicBlock, BasicBlock trueBlock) {
        super(VoidType.voidType, Operator.Br, basicBlock);
        this.addOperand(trueBlock);
    }

    public BrInst(BasicBlock basicBlock, Value cond, BasicBlock trueBlock, BasicBlock falseBlock) {
        super(VoidType.voidType, Operator.Br, basicBlock);
        // conversion handler
        Value condTmp = cond;
        if (!(cond.getType() instanceof IntegerType && ((IntegerType) cond.getType()).isI1())) {
            condTmp = BuildFactory.getInstance().buildBinary(basicBlock, Operator.Ne, cond, new ConstInt(0));
        }
        this.addOperand(condTmp);
        this.addOperand(trueBlock);
        this.addOperand(falseBlock);
    }

    public Value getTarget() {
        return this.getOperand(0);
    }

    public boolean isCondBr() {
        return this.getOperandList().size() == 3;
    }

    public Value getCond() {
        if (isCondBr()) {
            return this.getOperand(0);
        } else {
            return null;
        }
    }

    public BasicBlock getTrueLabel() {
        if (isCondBr()) {
            return (BasicBlock) this.getOperand(1);
        } else {
            return (BasicBlock) this.getOperand(0);
        }
    }

    public BasicBlock getFalseLabel() {
        if (isCondBr()) {
            return (BasicBlock) this.getOperand(2);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        if (this.getOperandList().size() == 1) {
            return "br label %" + this.getOperandList().get(0).getName();
        } else {
            return "br " + this.getOperandList().get(0).getType() + " " + this.getOperandList().get(0).getName() + ", label %" + this.getOperandList().get(1).getName() + ", label %" + this.getOperandList().get(2).getName();
        }
    }
}

