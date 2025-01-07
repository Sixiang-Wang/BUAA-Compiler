package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

public class FuncDefBlock {
    public FuncTypeBlock funcTypeBlock;
    public Token ident;
    public Token leftParent;

    public FuncFParamsBlock funcFParamsBlock;
    public Token rightParent;
    public BlockBlock blockBlock;
    public FuncDefBlock(FuncTypeBlock funcTypeBlock,Token ident,Token leftParent,FuncFParamsBlock funcFParamsBlock,Token rightParent,BlockBlock blockBlock){
        this.funcTypeBlock = funcTypeBlock;
        this.ident = ident;
        this.leftParent = leftParent;
        this.funcFParamsBlock = funcFParamsBlock;
        this.rightParent = rightParent;
        this.blockBlock = blockBlock;
    }

    public void print(){
        funcTypeBlock.print();
        FileController.printlnParser(ident.toString());
        FileController.printlnParser(leftParent.toString());
        if(funcFParamsBlock!=null){
            funcFParamsBlock.print();
        }
        FileController.printlnParser(rightParent.toString());
        blockBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.FuncDef));
    }
}
