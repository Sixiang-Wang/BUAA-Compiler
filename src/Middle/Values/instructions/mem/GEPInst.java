package Middle.Values.instructions.mem;

import Middle.Types.ArrayType;
import Middle.Types.PointerType;
import Middle.Types.Type;
import Middle.Values.BasicBlock;
import Middle.Values.GlobalVar;
import Middle.Values.Value;
import Middle.Values.instructions.Operator;

import java.util.List;

public class GEPInst extends MemInst {
    private Value target;

    public GEPInst(BasicBlock basicBlock, Value pointer, List<Value> indices) {
        super(new PointerType(getElementType(pointer, indices)), Operator.GEP, basicBlock);
        this.setName("%" + REG_NUMBER++);
        if (pointer instanceof GEPInst) {
            target = ((GEPInst) pointer).target;
        } else if (pointer instanceof AllocaInst) {
            target = pointer;
        } else if (pointer instanceof GlobalVar) {
            target = pointer;
        }
        this.addOperand(pointer);
        for (Value value : indices) {
            this.addOperand(value);
        }
    }

    public GEPInst(BasicBlock basicBlock, Value pointer, int offset) {
        this(basicBlock, pointer, ((ArrayType) ((PointerType) pointer.getType()).getTargetType()).offset2Index(offset));
    }
    public Value getTarget(){
        return target;
    }
    public Value getPointer() {
        return getOperandList().get(0);
    }

    private static Type getElementType(Value pointer, List<Value> indices) {
        Type type = pointer.getType();
        for (Value ignored : indices) {
            if (type instanceof ArrayType) {
                type = ((ArrayType) type).getElementType();
            } else if (type instanceof PointerType) {
                type = ((PointerType) type).getTargetType();
            } else {
                break;
            }
        }
        return type;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getName()).append(" = getelementptr ");
        if (getPointer().getType() instanceof PointerType && ((PointerType) getPointer().getType()).isString()) {
            s.append("inbounds ");
        }
        /*
        %1 = getelementptr [5 x i32], [5 x i32]* @a, i32 0, i32 3
        */
        s.append(((PointerType) getPointer().getType()).getTargetType()).append(", ");
        for (int i = 0; i < getOperandList().size(); i++) {
            if (i == 0) {
                s.append(getPointer().getType()).append(" ").append(getPointer().getName());
            } else {
                s.append(", ").append(getOperandList().get(i).getType()).append(" ").append(getOperandList().get(i).getName());
            }
        }
        return s.toString();
    }
}
