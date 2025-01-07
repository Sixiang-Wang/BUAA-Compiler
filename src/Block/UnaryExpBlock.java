package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class UnaryExpBlock {
    public PrimaryExpBlock primaryExpBlock = null;
    public Token ident = null;
    public Token leftParentToken = null;
    public FuncRParamsBlock funcRParamsBlock = null;
    public Token rightParentToken = null;
    public UnaryOpBlock unaryOpBlock = null;
    public UnaryExpBlock unaryExpBlock = null;

    public UnaryExpBlock(PrimaryExpBlock primaryExpBlock) {
        this.primaryExpBlock = primaryExpBlock;
    }

    public UnaryExpBlock(Token ident, Token leftParentToken, FuncRParamsBlock funcRParamsBlock, Token rightParentToken) {
        this.ident = ident;
        this.leftParentToken = leftParentToken;
        this.funcRParamsBlock = funcRParamsBlock;
        this.rightParentToken = rightParentToken;
    }

    public UnaryExpBlock(UnaryOpBlock unaryOpBlock, UnaryExpBlock unaryExpBlock) {
        this.unaryOpBlock = unaryOpBlock;
        this.unaryExpBlock = unaryExpBlock;
    }

    public void print(){
        if(primaryExpBlock!=null){
            primaryExpBlock.print();
        } else if (ident!=null) {
            ident.printlnParser();
            leftParentToken.printlnParser();
            if(funcRParamsBlock!=null){
                funcRParamsBlock.print();
            }
            rightParentToken.printlnParser();
        } else {
            unaryOpBlock.print();
            unaryExpBlock.print();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.UnaryExp));
    }
}
