package Middle.Values.instructions.terminator;

import Middle.Types.VoidType;
import Middle.Values.BasicBlock;
import Middle.Values.Value;
import Middle.Values.instructions.Operator;

public class RetInst extends TerminatorInst {
    public RetInst(BasicBlock basicBlock) {
        super(VoidType.voidType, Operator.Ret, basicBlock);
    }

    public RetInst(BasicBlock basicBlock, Value ret) {
        super(ret.getType(), Operator.Ret, basicBlock);
        this.addOperand(ret);
    }

    @Override
    public String toString() {
        if (getOperandList().size() == 1) {
            return "ret " + getOperandList().get(0).getType() + " " + getOperandList().get(0).getName();
        } else {
            return "ret void";
        }
    }

}

