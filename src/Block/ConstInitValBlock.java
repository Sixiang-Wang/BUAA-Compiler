package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

import java.util.ArrayList;
import java.util.List;

public class ConstInitValBlock {
    public List<ConstExpBlock> constExpBlockList = new ArrayList<>();
    public Token leftBraceToken = null;
    public List<Token> commaList = null;
    public Token rightBraceToken = null;
    public Token stringConst = null;

    public ConstInitValBlock(List<ConstExpBlock> constExpBlockList){
        this.constExpBlockList = constExpBlockList;
    }

    public ConstInitValBlock(Token leftBraceToken,List<ConstExpBlock> constExpBlockList,List<Token> commaList,Token rightBraceToken){
        this.leftBraceToken = leftBraceToken;
        this.constExpBlockList = constExpBlockList;
        this.commaList = commaList;
        this.rightBraceToken = rightBraceToken;
    }

    public ConstInitValBlock(Token stringConst){
        this.stringConst = stringConst;
    }

    public void print(){
        if(leftBraceToken!=null){
            FileController.printlnParser(leftBraceToken.toString());
            if(!constExpBlockList.isEmpty()){
                int i = 0;
                constExpBlockList.get(i).print();
                for(i=1;i< constExpBlockList.size();i++){
                    FileController.printlnParser(commaList.get(i-1).toString());
                    constExpBlockList.get(i).print();
                }
            }
            FileController.printlnParser(rightBraceToken.toString());
        } else if (stringConst!=null) {
            FileController.printlnParser(stringConst.toString());
        } else {
            constExpBlockList.get(0).print();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.ConstInitVal));
    }
}
