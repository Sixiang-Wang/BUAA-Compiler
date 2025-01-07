package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class VarDefBlock {
    public Token ident;
    public Token leftBracket;
    public ConstExpBlock constExpBlock;
    public Token rightBracket;
    public Token assign;
    public InitValBlock initValBlock;

    public VarDefBlock(Token ident, Token leftBracket, ConstExpBlock constExpBlock, Token rightBracket, Token assign, InitValBlock initValBlock) {
        this.ident = ident;
        this.leftBracket = leftBracket;
        this.constExpBlock = constExpBlock;
        this.rightBracket = rightBracket;
        this.assign = assign;
        this.initValBlock = initValBlock;
    }

    public void print(){
        ident.printlnParser();
        if(leftBracket!=null){
            leftBracket.printlnParser();
            constExpBlock.print();
            rightBracket.printlnParser();
        }
        if(assign!=null){
            assign.printlnParser();
            initValBlock.print();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.VarDef));
    }
}
