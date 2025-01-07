package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class CharacterBlock {
    public Token token;
    public CharacterBlock(Token token){
        this.token = token;
    }

    public void print() {
        FileController.printlnParser(token.toString());
        FileController.printlnParser(Parser.blockType.get(BlockType.Character));
    }
}
