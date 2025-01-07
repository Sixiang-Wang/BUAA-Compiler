package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

import java.util.List;

public class ConstDeclBlock {
    public Token constToken;
    public BTypeBlock bTypeBlock;
    public List<ConstDefBlock> constDefBlockList;
    public List<Token> commaList;
    public Token semicnToken;

    public ConstDeclBlock(Token constToken,BTypeBlock bTypeBlock,List<ConstDefBlock> constDefBlockList,List<Token> commaList,Token semicnToken){
        this.constToken = constToken;
        this.bTypeBlock = bTypeBlock;
        this.constDefBlockList = constDefBlockList;
        this.commaList = commaList;
        this.semicnToken = semicnToken;
    }

    public void print(){

        FileController.printlnParser(constToken.toString());
        bTypeBlock.print();
        int i=0;
        constDefBlockList.get(i).print();
        for(i=1;i<constDefBlockList.size();i++){
            FileController.printlnParser(commaList.get(i-1).toString());
            constDefBlockList.get(i).print();
        }
        FileController.printlnParser(semicnToken.toString());
        FileController.printlnParser(Parser.getBlockType(BlockType.ConstDecl));
    }
}
