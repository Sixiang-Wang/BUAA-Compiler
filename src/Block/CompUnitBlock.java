package Block;

import Tool.FileController;
import Frontend.Parser;

import java.util.List;

public class CompUnitBlock {
    public List<DeclBlock> declBlockList;
    public List<FuncDefBlock> funcDefBlockList;
    public MainFuncDefBlock mainFuncDefBlock;

    public CompUnitBlock(List<DeclBlock> declBlockList,List<FuncDefBlock> funcDefBlockList,MainFuncDefBlock mainFuncDefBlock){
        this.declBlockList = declBlockList;
        this.funcDefBlockList = funcDefBlockList;
        this.mainFuncDefBlock = mainFuncDefBlock;
    }

    public void print(){
        for(DeclBlock declBlock:declBlockList){
            declBlock.print();
        }
        for(FuncDefBlock funcDefBlock:funcDefBlockList){
            funcDefBlock.print();
        }
        mainFuncDefBlock.print();
        FileController.printlnParser(Parser.getBlockType(BlockType.CompUnit));
    }
}
