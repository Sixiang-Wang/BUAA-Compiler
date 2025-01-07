package Symbol;

import Middle.Values.Value;
import Tool.FileController;

import java.util.LinkedHashMap;

public class SymbolTable {
    public int id;
    public int fatherId;
    public LinkedHashMap<String,Symbol> directory = new LinkedHashMap<>();
    public LinkedHashMap<String, Value> valueTable = new LinkedHashMap<>();
    public LinkedHashMap<String,Integer> constTable = new LinkedHashMap<>();


    public boolean isFunc;
    public BType bType;

    public SymbolTable(int id, int fatherId, LinkedHashMap<String, Symbol> directory, boolean isFunc, BType bType) {
        this.id = id;
        this.fatherId = fatherId;
        this.directory = directory;
        this.isFunc = isFunc;
        this.bType = bType;
    }

    public SymbolTable(){
    }
    public SymbolTable(int id,int fatherId){
        this.id = id;
        this.fatherId = fatherId;
    }

    public void print(){
        directory.forEach((string, symbol) -> {
            String result = id+" "+symbol.toString();

            FileController.printlnSymbol(result);
            System.out.println(result);
        });
    }
}
