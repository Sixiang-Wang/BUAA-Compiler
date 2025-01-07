package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class RelExpBlock {
    public AddExpBlock addExpBlock;
    public Token op;
    public RelExpBlock relExpBlock;


    public RelExpBlock(AddExpBlock addExpBlock, Token op, RelExpBlock relExpBlock) {
        this.addExpBlock = addExpBlock;
        this.op = op;
        this.relExpBlock = relExpBlock;
    }

    public void print(){
        addExpBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.RelExp));
        if(op!=null){
            FileController.printlnParser(op.toString());
            relExpBlock.print();
        }
    }
}
