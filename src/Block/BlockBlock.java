package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

import java.util.List;

public class BlockBlock {
    public Token leftBraceToken,rightBraceToken;
    public List<BlockItemBlock> blockItemBlockList;
    public BlockBlock(Token leftBraceToken,List<BlockItemBlock> blockItemBlockList,Token rightBraceToken){
        this.leftBraceToken = leftBraceToken;
        this.blockItemBlockList = blockItemBlockList;
        this.rightBraceToken = rightBraceToken;
    }

    public void print(){
        FileController.printlnParser(leftBraceToken.toString());
        blockItemBlockList.forEach(blockItemBlock -> {
            blockItemBlock.print();
        });
        FileController.printlnParser(rightBraceToken.toString());

        FileController.printlnParser(Parser.blockType.get(BlockType.Block));
    }
}
