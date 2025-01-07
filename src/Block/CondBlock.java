package Block;

import Tool.FileController;
import Frontend.Parser;

public class CondBlock {
    public LOrExpBlock lOrExpBlock;
    public CondBlock(LOrExpBlock lOrExpBlock){
        this.lOrExpBlock = lOrExpBlock;
    }

    public void print(){
        lOrExpBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.Cond));
    }
}
