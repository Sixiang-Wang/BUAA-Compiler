package Block;

import Token.*;
import Tool.FileController;
import Frontend.Parser;

public class ConstDefBlock {
    public Token ident;
    public Token leftBrack;
    public ConstExpBlock constExpBlock;
    public Token rightBrack;
    public Token assign;
    public ConstInitValBlock constInitValBlock;

    public ConstDefBlock(Token ident, Token leftBrack, ConstExpBlock constExpBlock, Token rightBrack, Token assign, ConstInitValBlock constInitValBlock) {
        this.ident = ident;
        this.leftBrack = leftBrack;
        this.constExpBlock = constExpBlock;
        this.rightBrack = rightBrack;
        this.assign = assign;
        this.constInitValBlock = constInitValBlock;
    }
    public void print(){
        FileController.printlnParser(ident.toString());
        if(leftBrack!=null){
            FileController.printlnParser(leftBrack.toString());
            constExpBlock.print();
            FileController.printlnParser(rightBrack.toString());
        }
        FileController.printlnParser(assign.toString());
        constInitValBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.ConstDef));
    }
}
