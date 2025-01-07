package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class MainFuncDefBlock {
    public Token intToken;
    public Token mainToken;
    public Token leftParentToken;
    public Token rightParentToken;
    public BlockBlock blockBlock;

    public MainFuncDefBlock(Token intToken, Token mainToken, Token leftParentToken, Token rightParentToken, BlockBlock blockBlock) {
        this.intToken = intToken;
        this.mainToken = mainToken;
        this.leftParentToken = leftParentToken;
        this.rightParentToken = rightParentToken;
        this.blockBlock = blockBlock;
    }

    public void print(){
        intToken.printlnParser();
        mainToken.printlnParser();
        leftParentToken.printlnParser();
        rightParentToken.printlnParser();
        blockBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.MainFuncDef));
    }
}
