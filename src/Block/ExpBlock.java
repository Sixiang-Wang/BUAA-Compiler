package Block;

import Tool.FileController;
import Frontend.Parser;

public class ExpBlock {
    public AddExpBlock addExpBlock;
    public ExpBlock(AddExpBlock addExpBlock){
        this.addExpBlock = addExpBlock;
    }

    public void print(){
        addExpBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.Exp));
    }
}
