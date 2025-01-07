package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class LValBlock {
    public Token ident;
    public Token leftBracket;
    public ExpBlock expBlock;
    public Token rightBracket;

    public LValBlock(Token ident, Token leftBracket, ExpBlock expBlock, Token rightBracket) {
        this.ident = ident;

        this.leftBracket = leftBracket;
        this.expBlock = expBlock;
        this.rightBracket = rightBracket;
    }

    public void print(){
        FileController.printlnParser(ident.toString());
        if(leftBracket!=null){
            leftBracket.printlnParser();
            expBlock.print();
            rightBracket.printlnParser();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.LVal));
    }
}
