package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class PrimaryExpBlock {
    public Token leftParentToken = null;
    public ExpBlock expBlock = null;
    public Token rightParentToken = null;
    public LValBlock lValBlock = null;
    public NumberBlock numberBlock = null;
    public CharacterBlock characterBlock = null;

    public PrimaryExpBlock(NumberBlock numberBlock){
        this.numberBlock = numberBlock;
    }
    public PrimaryExpBlock(CharacterBlock characterBlock){
        this.characterBlock = characterBlock;
    }

    public PrimaryExpBlock(LValBlock lValBlock){
        this.lValBlock = lValBlock;
    }

    public PrimaryExpBlock(Token leftParentToken, ExpBlock expBlock, Token rightParentToken) {
        this.leftParentToken = leftParentToken;
        this.expBlock = expBlock;
        this.rightParentToken = rightParentToken;
    }

    public void print(){
        if(expBlock!=null){
            leftParentToken.printlnParser();
            expBlock.print();
            rightParentToken.printlnParser();
        }
        else if(lValBlock != null){
            lValBlock.print();
        }else if(numberBlock!=null){
            numberBlock.print();
        } else if (characterBlock!=null) {
            characterBlock.print();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.PrimaryExp));
    }
}
