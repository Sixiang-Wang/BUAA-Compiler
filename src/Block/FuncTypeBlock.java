package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class FuncTypeBlock {
    public Token type;
    public FuncTypeBlock(Token type){
        this.type = type;
    }

    public void print(){
        FileController.printlnParser(type.toString());
        FileController.printlnParser(Parser.getBlockType(BlockType.FuncType));
    }
}
