package Middle.Values.instructions.mem;

import Middle.Types.ArrayType;
import Middle.Types.PointerType;
import Middle.Values.BasicBlock;
import Middle.Values.Value;
import Middle.Values.instructions.Operator;

public class LoadInst extends MemInst {

    public LoadInst(BasicBlock basicBlock, Value pointer) {
        super(((PointerType) pointer.getType()).getTargetType(), Operator.Load, basicBlock);
        this.setName("%" + REG_NUMBER++);
        if (getType() instanceof ArrayType) {
            setType(new PointerType(((ArrayType) getType()).getElementType()));
        }
        this.addOperand(pointer);
    }

    public Value getPointer() {
        return getOperandList().get(0);
    }


    @Override
    public String toString() {
        return getName() + " = load " + getType() + ", " + getPointer().getType() + " " + getPointer().getName();
    }
}
