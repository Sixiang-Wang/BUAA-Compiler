package Block;

import Symbol.SymbolTable;
import Tool.FileController;
import Frontend.Parser;

public class ConstExpBlock {
    public AddExpBlock addExpBlock;
    public ConstExpBlock(AddExpBlock addExpBlock){
        this.addExpBlock = addExpBlock;
    }

    public void print(){
        addExpBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.ConstExp));
    }


}
