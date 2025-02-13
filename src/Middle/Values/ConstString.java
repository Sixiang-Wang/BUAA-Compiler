package Middle.Values;

import Middle.Types.IntegerType;
import Middle.Types.PointerType;

public class ConstString extends Const {
    public String value;
    public int length;

    public ConstString(String value) {
        super("\"" + value.replace("\n", "\\n") + "\"", new PointerType(IntegerType.i8));
        this.length = value.length() + 1;
        this.value = value.replace("\n", "\\0a") + "\\00";
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "[" + length + " x " + ((PointerType) getType()).getTargetType() + "] c\"" + value + "\"";
    }
}
