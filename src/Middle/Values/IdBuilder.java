package Middle.Values;

public class IdBuilder {
    private static final IdBuilder instance = new IdBuilder();
    private int count;

    public IdBuilder() {
        count = 0;
    }

    public static IdBuilder getInstance() {
        return instance;
    }

    public String getId() {
        return "id_" + ++count;
    }
}
