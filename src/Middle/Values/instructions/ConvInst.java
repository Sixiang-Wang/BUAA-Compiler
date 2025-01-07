package Middle.Values.instructions;

import Middle.Types.IntegerType;
import Middle.Types.PointerType;
import Middle.Types.VoidType;
import Middle.Values.BasicBlock;
import Middle.Values.Value;

public class ConvInst extends Instruction {

    public ConvInst(BasicBlock basicBlock, Operator op, Value value) {
        super(VoidType.voidType, op, basicBlock);
        this.setName("%" + REG_NUMBER++);

        if (op == Operator.Zext) {
            setType(IntegerType.i32);
        } else if(op == Operator.Trunc){
            setType(IntegerType.i8);
        }
        addOperand(value);
    }



    @Override
    public String toString() {
        IntegerType prevType = (IntegerType) getOperandList().get(0).getType();
        if (getOperator() == Operator.Zext) {
            return getName() + " = zext "+prevType.getBitString()+" " + getOperandList().get(0).getName() + " to i32";
        } else if (getOperator() == Operator.Trunc) {
            return getName() + " = trunc "+prevType.getBitString()+" " + getOperandList().get(0).getName() + " to i8";
        }  else {
            return null;
        }
    }
}
