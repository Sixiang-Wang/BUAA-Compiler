package Middle.Types;

public class VoidType implements Type {
    public static final VoidType voidType = new VoidType();

    @Override
    public String toString() {
        return "void";
    }
}
