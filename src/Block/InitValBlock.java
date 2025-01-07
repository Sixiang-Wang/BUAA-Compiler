package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

import java.util.List;

public class InitValBlock {

    public List<ExpBlock> expBlockList = null;
    public Token leftBraceToken = null;
    public List<Token> commaList = null;
    public Token rightBraceToken = null;
    public Token stringConst = null;

    public InitValBlock(List<ExpBlock> expBlockList){
        this.expBlockList = expBlockList;
    }

    public InitValBlock(Token leftBraceToken, List<ExpBlock> expBlockList, List<Token> commaList, Token rightBraceToken){
        this.leftBraceToken = leftBraceToken;
        this.expBlockList = expBlockList;
        this.rightBraceToken = rightBraceToken;
        this.commaList = commaList;
    }

    public InitValBlock(Token stringConst){
        this.stringConst = stringConst;
    }

    public void print(){
        if(leftBraceToken!=null){
            FileController.printlnParser(leftBraceToken.toString());
            if(!expBlockList.isEmpty()){
                int i = 0;
                expBlockList.get(i).print();
                for(i=1;i< expBlockList.size();i++){
                    FileController.printlnParser(commaList.get(i-1).toString());
                    expBlockList.get(i).print();
                }
            }
            FileController.printlnParser(rightBraceToken.toString());
        } else if (stringConst!=null) {
            FileController.printlnParser(stringConst.toString());
        } else {
            expBlockList.get(0).print();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.InitVal));
    }
}
