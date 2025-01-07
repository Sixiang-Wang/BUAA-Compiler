package Symbol;

import Middle.Values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SymbolTableList {
    /**
     * 用来生成中间代码
     */
    public List<SymbolTable> symbolTableList = new ArrayList<>();
    public int tableId = 0;
    public void addSymbolTable(){
        int fatherId = tableId;
        tableId = symbolTableList.size()+1;
        symbolTableList.add(new SymbolTable(tableId,fatherId));
    }

    public void removeSymbolTable(){
        tableId = symbolTableList.get(tableId-1).fatherId;
    }

    public Map<String, Value> getCurSymbolTable(){
        return symbolTableList.get(tableId-1).valueTable;
    }
    public void addSymbol(String name, Value value){
        getCurSymbolTable().put(name,value);
    }
    public void addGlobalSymbol(String name,Value value){
        symbolTableList.get(0).valueTable.put(name,value);
    }
    public Value getValue(String ident){
        int id = tableId;
        SymbolTable symbolTable = symbolTableList.get(id-1);
        while(id>0) {
            if (symbolTable.valueTable.containsKey(ident)) {
                return symbolTable.valueTable.get(ident);
            }
            id = symbolTable.fatherId;
            if(id<=0){
                return null;
            }
            symbolTable = symbolTableList.get(id-1);
        }
        return null;
    }

    public Value getGlobalValue(String ident){
        SymbolTable symbolTable = symbolTableList.get(0);
        if (symbolTable.valueTable.containsKey(ident)) {
            return symbolTable.valueTable.get(ident);
        }
        return null;
    }

    public Map<String, Integer> getCurConstTable() {
        return symbolTableList.get(tableId - 1).constTable;
    }

    public void addConst(String name, Integer value) {
        getCurConstTable().put(name, value);
    }
    public Integer getConst(String ident){
        int id = tableId;
        SymbolTable symbolTable = symbolTableList.get(id-1);
        while(id>0) {
            if (symbolTable.constTable.containsKey(ident)) {
                return symbolTable.constTable.get(ident);
            }
            id = symbolTable.fatherId;
            if(id<=0){
                return null;
            }
            symbolTable = symbolTableList.get(id-1);
        }
        return null;
    }
}
