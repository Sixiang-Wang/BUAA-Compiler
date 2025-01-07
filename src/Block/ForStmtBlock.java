package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class ForStmtBlock {
    public LValBlock lValBlock;
    public Token op;
    public ExpBlock expBlock;
    public ForStmtBlock(LValBlock lValBlock,Token op,ExpBlock expBlock){
        this.lValBlock = lValBlock;
        this.op = op;
        this.expBlock = expBlock;
    }

    public void print(){
        lValBlock.print();
        FileController.printlnParser(op.toString());
        expBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.ForStmt));
    }
}
