package Middle.Values;

import Middle.Types.ArrayType;
import Middle.Types.IntegerType;
import Middle.Types.Type;

import java.util.ArrayList;
import java.util.List;

public class ConstArray extends Const {
    public Type elementType;
    private final List<Value> array;
    private boolean init = false;

    public ConstArray(Type type, Type elementType) {
        super("", type);
        this.elementType = elementType;
        this.array = new ArrayList<>();
        if (elementType instanceof ArrayType) {
            for (int i = 0; i < ((ArrayType) type).getLength(); i++) {
                array.add(new ConstArray(elementType, ((ArrayType) elementType).getElementType()));
            }
        } else {
            for (int i = 0; i < ((ArrayType) type).getLength(); i++) {
                array.add(ConstInt.ZERO);
            }
        }
    }


    public boolean isInit() {
        return init || !allZero();
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public List<Value> get1DArray() {
        List<Value> result = new ArrayList<>();
        for (Value value : array) {
            if (value instanceof ConstArray) {
                result.addAll(((ConstArray) value).get1DArray());
            } else {
                result.add(value);
            }
        }
        return result;
    }

    public void storeValue(int offset, Value value) {
        // recursion
        if (elementType instanceof ArrayType) {
            ((ConstArray) (array.get(offset / ((ArrayType) elementType).getCapacity()))).storeValue(offset % ((ArrayType) elementType).getCapacity(), value);
        } else {
            array.set(offset, value);
        }
    }

    public boolean allZero() {
        for (Value value : array) {
            if (value instanceof ConstInt) {
                if (((ConstInt) value).getValue() != 0) {
                    return false;
                }
            } else {
                if (!((ConstArray) value).allZero()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (allZero()) {
            return this.getType().toString() + " " + "zeroinitializer";
        } else {
            if(elementType== IntegerType.i8){
                StringBuilder sb = new StringBuilder();
                sb.append(this.getType().toString()).append(" ").append("c\"");
                int i;
                for (i = 0; i < array.size(); i++) {

                        ConstInt constInt = (ConstInt)array.get(i);
                        if(constInt.value>0){
                            sb.append((char) constInt.value);
                        }else {
                            sb.append("\\00");
                        }

                }
                for(;i<((ArrayType)this.getType()).getLength();i++){
                    sb.append("\\00");
                }
                sb.append("\"");
                return sb.toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(this.getType().toString()).append(" ").append("[");
            for (int i = 0; i < array.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(array.get(i).toString());
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
