package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

import java.util.List;

public class VarDeclBlock {
    public BTypeBlock bTypeBlock;
    public List<VarDefBlock> varDefBlockList;
    public List<Token> commaList;
    public Token semicn;

    public VarDeclBlock(BTypeBlock bTypeBlock, List<VarDefBlock> varDefBlockList, List<Token> commaList, Token semicn) {
        this.bTypeBlock = bTypeBlock;
        this.varDefBlockList = varDefBlockList;
        this.commaList = commaList;
        this.semicn = semicn;
    }

    public void print(){
        bTypeBlock.print();
        int i = 0;
        varDefBlockList.get(i).print();
        for (i=1;i<varDefBlockList.size();i++){
            commaList.get(i-1).printlnParser();
            varDefBlockList.get(i).print();
        }
        semicn.printlnParser();
        FileController.printlnParser(Parser.getBlockType(BlockType.VarDecl));
    }
}
