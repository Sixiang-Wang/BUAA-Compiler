package Middle.Values.instructions.mem;

import Middle.Types.ArrayType;
import Middle.Types.PointerType;
import Middle.Types.Type;
import Middle.Values.BasicBlock;
import Middle.Values.instructions.Operator;

public class AllocaInst extends MemInst {
    private boolean isConst;
    private Type allocaType;

    public AllocaInst(BasicBlock basicBlock, boolean isConst, Type allocaType) {
        super(new PointerType(allocaType), Operator.Alloca, basicBlock);
        this.setName("%" + REG_NUMBER++);
        this.isConst = isConst;
        this.allocaType = allocaType;
        if (allocaType instanceof ArrayType) {
            if (((ArrayType) allocaType).getLength() == -1) {
                this.allocaType = new PointerType(((ArrayType) allocaType).getElementType());
                setType(new PointerType(this.allocaType));
            }
        }
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public Type getAllocaType() {
        return allocaType;
    }

    @Override
    public String toString() {
        return this.getName() + " = alloca " + this.getAllocaType();
    }
}
