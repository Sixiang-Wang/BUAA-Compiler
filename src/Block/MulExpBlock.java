package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class MulExpBlock {
    public UnaryExpBlock unaryExpBlock;
    public Token op;
    public MulExpBlock mulExpBlock;

    public MulExpBlock(UnaryExpBlock unaryExpBlock, Token op, MulExpBlock mulExpBlock) {
        this.unaryExpBlock = unaryExpBlock;
        this.op = op;
        this.mulExpBlock = mulExpBlock;
    }

    public void print(){
        unaryExpBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.MulExp));
        if(op!=null){
            op.printlnParser();
            mulExpBlock.print();
        }
    }
}
