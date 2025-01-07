package Error;

public class Err {
    public Integer lineNum;
    public ErrType type;
    public String context;

    public Err(Integer lineNum, ErrType type) {
        this.lineNum = lineNum;
        this.type = type;
        context = lineNum + " " + type + "\n";
    }


    public boolean equals(Err err) {
        return this.lineNum.equals(err.lineNum);
    }
}
