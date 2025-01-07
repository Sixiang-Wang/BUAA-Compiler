package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class NumberBlock {
    public Token token;

    public NumberBlock(Token token) {
        this.token = token;
    }

    public void print(){
        token.printlnParser();
        FileController.printlnParser(Parser.getBlockType(BlockType.Number));
    }
}
