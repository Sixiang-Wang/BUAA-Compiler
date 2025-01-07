package Block;

import Symbol.SymbolTable;
import Token.*;
import Tool.FileController;
import Frontend.Parser;

public class AddExpBlock {
    public MulExpBlock mulExpBlock;
    public Token op;
    public AddExpBlock addExpBlock;

    public AddExpBlock(MulExpBlock mulExpBlock){
        this.mulExpBlock = mulExpBlock;
    }

    public AddExpBlock(MulExpBlock mulExpBlock,Token op,AddExpBlock addExpBlock){
        this.addExpBlock = addExpBlock;
        this.op = op;
        this.mulExpBlock = mulExpBlock;
    }

    public void print(){
        mulExpBlock.print();
        FileController.printlnParser(Parser.blockType.get(BlockType.AddExp));
        if(op!=null){
            FileController.printlnParser(op.toString());
            addExpBlock.print();
        }
    }



}
