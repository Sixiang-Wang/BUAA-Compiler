package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class LAndExpBlock {
    public EqExpBlock eqExpBlock;
    public LAndExpBlock lAndExpBlock;
    public Token op;
    public LAndExpBlock(LAndExpBlock lAndExpBlock,Token op,EqExpBlock eqExpBlock){
        this.lAndExpBlock = lAndExpBlock;
        this.eqExpBlock = eqExpBlock;
        this.op = op;
    }

    public void print(){
        eqExpBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.LAndExp));
        if(op!=null){
            FileController.printlnParser(op.toString());
            lAndExpBlock.print();
        }
    }
}
