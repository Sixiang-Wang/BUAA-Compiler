package Middle.Types;


import Middle.Values.ConstInt;
import Middle.Values.Value;

import java.util.ArrayList;
import java.util.List;

public class ArrayType implements Type {
    /**
     * 不会有多维数组，不慌
     */
    private final Type elementType;
    private final int length;

    public ArrayType(Type elementType) {
        this.elementType = elementType;
        this.length = 0;
    }

    public ArrayType(Type elementType, int length) {
        this.elementType = elementType;
        this.length = length;
    }

    public Type getElementType() {
        return elementType;
    }

    public boolean isIntArray() {
        return elementType instanceof IntegerType && ((IntegerType) elementType).isI32();
    }

    public boolean isString() {
        return elementType instanceof IntegerType && ((IntegerType) elementType).isI8();
    }

    public int getLength() {
        return length;
    }

    public int getCapacity() {
        return length;
    }

    /**
     * 先产生0，再产生offset
     * 方便GEP调用
     * %1 = getelementptr [5 x i32], [5 x i32]* @a, i32 0, i32 3
     * @param offset
     * @return
     */
    public List<Value> offset2Index(int offset) {
        List<Value> index = new ArrayList<>();
        Type type = this;
        while (type instanceof ArrayType) {
            index.add(new ConstInt(offset / ((ArrayType) type).getCapacity()));
            offset %= ((ArrayType) type).getCapacity();
            type = ((ArrayType) type).getElementType();
        }
        index.add(new ConstInt(offset));
        return index;
    }

    @Override
    public String toString() {
        return "[" + length + " x " + elementType.toString() + "]";
    }

}
