package Middle.Values;
import Middle.Types.Type;

public class Value {
    private String name;
    private Type type;
    public static int REG_NUMBER = 0; // LLVM 中的寄存器编号
    private final String id; // LLVM 中的 Value 的唯一编号

    public Value(String name, Type type) {
        this.name = name;
        this.type = type;
        this.id = IdBuilder.getInstance().getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public String getId() {
        return id;
    }

    public String getNameId() {
        if (isNumber()){
            return getName();
        }
        if (isGlobal()) {
            return getGlobalName();
        }
        return getName() + "_" + getId();
    }

    public String getGlobalName() {
        return name.replaceAll("@", "");
    }

    public boolean isNumber() {
        return this instanceof ConstInt;
    }

    public int getNumber() {
        return Integer.parseInt(name);
    }

    public boolean isGlobal() {
        return name.startsWith("@");
    }

    @Override
    public String toString() {

        return type.toString() + " " + name;
    }

}
