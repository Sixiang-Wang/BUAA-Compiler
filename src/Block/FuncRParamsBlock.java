package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

import java.util.List;

public class FuncRParamsBlock {
    public List<ExpBlock> expBlockList;
    public List<Token> commaList;
    public FuncRParamsBlock(List<ExpBlock> expBlockList,List<Token> commaList){
        this.expBlockList = expBlockList;
        this.commaList = commaList;
    }

    public void print(){
        int i = 0;
        expBlockList.get(i).print();
        for(i=1;i<expBlockList.size();i++){
            FileController.printlnParser(commaList.get(i-1).toString());
            expBlockList.get(i).print();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.FuncRParams));
    }
}
