package Backend;

import Middle.Values.Value;

public class Reg {
    public String reg;
    public Integer offset;
    public Value value;

    public Reg(String reg, Integer offset, Value value) {
        this.reg = reg;
        this.offset = offset;
        this.value = value;
    }

}
