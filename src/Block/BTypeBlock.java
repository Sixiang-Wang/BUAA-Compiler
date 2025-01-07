package Block;

import Token.Token;
import Tool.FileController;

public class BTypeBlock {
    public Token token;
    public BTypeBlock(Token token){
        this.token = token;
    }

    public void print(){
        FileController.printlnParser(token.toString());
    }
}
