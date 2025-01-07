package Middle.Values.instructions.mem;

import Middle.Values.BasicBlock;
import Middle.Values.Value;
import Middle.Values.instructions.Operator;

public class StoreInst extends MemInst {
    public StoreInst(BasicBlock basicBlock, Value pointer, Value value) {
        super(value.getType(), Operator.Store, basicBlock);
        this.addOperand(value);
        this.addOperand(pointer);
    }

    public Value getValue() {
        return getOperandList().get(0);
    }

    public Value getPointer() {
        return getOperandList().get(1);
    }

    @Override
    public String toString() {
        return "store " + getValue().getType() + " " + getValue().getName() + ", " + getPointer().getType() + " " + getPointer().getName();
    }
}
