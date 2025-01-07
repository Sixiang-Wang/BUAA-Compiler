package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class EqExpBlock {
    public RelExpBlock relExpBlock;
    public EqExpBlock eqExpBlock;
    public Token op;

    public EqExpBlock(RelExpBlock relExpBlock){
        this.relExpBlock = relExpBlock;
    }

    public EqExpBlock(RelExpBlock relExpBlock,Token op,EqExpBlock eqExpBlock){
        this.eqExpBlock = eqExpBlock;
        this.op = op;
        this.relExpBlock = relExpBlock;
    }

    public void print(){
        relExpBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.EqExp));
        if(op!=null){
            FileController.printlnParser(op.toString());
            eqExpBlock.print();
        }
    }
}
