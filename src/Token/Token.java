package Token;

import Tool.FileController;

public class Token {
    public TokenType type;
    public String token;
    public Integer line;
    public Token(TokenType tokenType,String token,Integer line) {
        this.type = tokenType;
        this.token = token;
        this.line = line;
    }

    @Override
    public String toString(){
        return type.toString()+" "+token;
    }

    public void printlnParser(){
        FileController.printlnParser(this.toString());
    }
}
