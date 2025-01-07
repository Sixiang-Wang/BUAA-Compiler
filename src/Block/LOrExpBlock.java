package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class LOrExpBlock {
    public LAndExpBlock lAndExpBlock;
    public Token op;
    public LOrExpBlock lOrExpBlock;

    public LOrExpBlock(LAndExpBlock lAndExpBlock, Token op, LOrExpBlock lOrExpBlock) {
        this.lAndExpBlock = lAndExpBlock;
        this.op = op;
        this.lOrExpBlock = lOrExpBlock;
    }

    public void print(){
        lAndExpBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.LOrExp));
        if(op!=null){
            FileController.printlnParser(op.toString());
            lOrExpBlock.print();
        }
    }
}
