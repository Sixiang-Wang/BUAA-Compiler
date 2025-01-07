package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class UnaryOpBlock {
    public Token op;

    public UnaryOpBlock(Token op) {
        this.op = op;
    }

    public void print(){
        op.printlnParser();
        FileController.printlnParser(Parser.getBlockType(BlockType.UnaryOp));
    }
}
