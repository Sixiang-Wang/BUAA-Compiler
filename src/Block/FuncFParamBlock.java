package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class FuncFParamBlock {
    public BTypeBlock bTypeBlock;
    public Token ident;
    public Token leftBracket;
    public Token rightBracket;
    public FuncFParamBlock(BTypeBlock bTypeBlock, Token ident, Token leftBracket, Token rightBracket){
        this.bTypeBlock = bTypeBlock;
        this.ident = ident;
        this.leftBracket = leftBracket;
        this.rightBracket = rightBracket;
    }

    public void print(){
        bTypeBlock.print();
        FileController.printlnParser(ident.toString());
        if(leftBracket !=null){
            FileController.printlnParser(leftBracket.toString());
            FileController.printlnParser(rightBracket.toString());
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.FuncFParam));
    }
}
