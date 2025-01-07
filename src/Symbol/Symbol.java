package Symbol;

import Middle.Values.Value;

import java.util.ArrayList;
import java.util.List;

public class Symbol {
    public int id = 0;
    public int tableId = 0;
    public String token = null;
    public int isFunc = 0; //0:变量 1:函数
    public int dimension = 0;
    public BType bType = null;
    public int isConst = 0;

    public Value value;
    public List<Symbol> funcParams = new ArrayList<>();
    public Symbol(String token, int isFunc, int dimension, BType bType, int isConst){
        this.token = token;
        this.isFunc = isFunc;
        this.dimension = dimension;
        this.bType = bType;
        this.isConst = isConst;
    }

    public Symbol(String token, int isFunc, int dimension, BType bType, int isConst,List<Symbol> funcParams){
        this.token = token;
        this.isFunc = isFunc;
        this.dimension = dimension;
        this.bType = bType;
        this.isConst = isConst;
        this.funcParams = funcParams;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(isConst==1){
            stringBuilder.append("Const");
        }
        if(bType==BType.INT){
            stringBuilder.append("Int");
        } else if (bType==BType.CHAR) {
            stringBuilder.append("Char");
        } else {
            stringBuilder.append("Void");
        }
        if(isFunc==1){
            stringBuilder.append("Func");
        } else if (dimension>=1) {
            stringBuilder.append("Array");
        }

        return this.token+" "+ stringBuilder;
    }
}
