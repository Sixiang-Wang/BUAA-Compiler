package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

import java.util.List;

public class FuncFParamsBlock {
    public List<FuncFParamBlock> funcFParamBlockList;

    public List<Token> commaList;
    public FuncFParamsBlock(List<FuncFParamBlock> funcFParamBlockList,List<Token> commaList){
        this.funcFParamBlockList = funcFParamBlockList;
        this.commaList = commaList;
    }

    public void print(){
        int i = 0;
        funcFParamBlockList.get(i).print();
        for(i=1;i<funcFParamBlockList.size();i++){
            FileController.printlnParser(commaList.get(i-1).toString());
            funcFParamBlockList.get(i).print();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.FuncFParams));
    }
}
