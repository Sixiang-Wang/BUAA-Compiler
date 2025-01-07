package Block;

import Token.Token;
import Tool.FileController;
import Frontend.Parser;

import java.util.ArrayList;
import java.util.List;

public class StmtBlock {
    public StmtType type = null;
    public LValBlock lValBlock = null;
    public Token assignToken = null;
    public ExpBlock expBlock = null;
    public Token semicnToken = null;
    public BlockBlock blockBlock = null;
    public Token ifToken = null;
    public Token leftParentToken = null;
    public CondBlock condBlock = null;
    public Token rightParentToken = null;
    public List<StmtBlock> stmtBlockList = new ArrayList<>();
    public Token elseToken = null;
    public Token forToken = null;
    public ForStmtBlock forStmtBlock1 = null;
    public ForStmtBlock forStmtBlock2 = null;
    public Token forSemicn1 = null;
    public Token forSemicn2 = null;
    public StmtBlock stmtBlock = null;
    public Token breakOrContinueToken = null;
    public Token continueToken = null;
    public Token returnToken = null;
    public List<ExpBlock> expBlockList = new ArrayList<>();
    public Token getToken = null;
    public Token printfToken = null;
    public Token stringConst = null;
    public List<Token> commaList = new ArrayList<>();

    public StmtBlock(StmtType type, LValBlock lValBlock, Token assignToken, ExpBlock expBlock, Token semicnToken) {
        // LVal '=' Exp ';'
        this.type = type;
        this.lValBlock = lValBlock;
        this.assignToken = assignToken;
        this.expBlock = expBlock;
        this.semicnToken = semicnToken;
    }

    public StmtBlock(StmtType type, ExpBlock expBlock, Token semicnToken) {
        // [Exp] ';'
        this.type = type;
        this.expBlock = expBlock;
        this.semicnToken = semicnToken;
    }

    public StmtBlock(StmtType type, BlockBlock blockBlock) {
        // Block
        this.type = type;
        this.blockBlock = blockBlock;
    }

    public StmtBlock(StmtType type, Token ifToken, Token leftParentToken, CondBlock condBlock, Token rightParentToken, List<StmtBlock> stmtBlockList, Token elseToken) {
        // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        this.type = type;
        this.ifToken = ifToken;
        this.leftParentToken = leftParentToken;
        this.condBlock = condBlock;
        this.rightParentToken = rightParentToken;
        this.stmtBlockList = stmtBlockList;
        this.elseToken = elseToken;
    }

    public StmtBlock(StmtType type, Token forToken, Token leftParentToken, ForStmtBlock forStmtBlock1,Token forSemicn1, CondBlock condBlock, Token forSemicn2, ForStmtBlock forStmtBlock2, Token rightParentToken, StmtBlock stmtBlock) {
        // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        this.type = type;
        this.forToken = forToken;
        this.leftParentToken = leftParentToken;
        this.forStmtBlock1 = forStmtBlock1;
        this.forSemicn1 = forSemicn1;
        this.condBlock = condBlock;
        this.forSemicn2 = forSemicn2;
        this.forStmtBlock2 = forStmtBlock2;
        this.rightParentToken = rightParentToken;
        this.stmtBlock = stmtBlock;
    }

    public StmtBlock(StmtType type, Token breakOrContinueToken, Token semicnToken) {
        // 'break' ';' | 'continue' ';'
        this.type = type;
        this.breakOrContinueToken = breakOrContinueToken;
        this.semicnToken = semicnToken;
    }


    public StmtBlock(StmtType type, Token returnToken, ExpBlock expBlock, Token semicnToken) {
        // 'return' [Exp] ';'
        this.type = type;
        this.returnToken = returnToken;
        this.expBlock = expBlock;
        this.semicnToken = semicnToken;
    }

    public StmtBlock(StmtType type, LValBlock lValBlock, Token assignToken, Token getToken, Token leftParentToken, Token rightParentToken, Token semicnToken) {
        // LVal '=' 'getint' '(' ')' ';' | LVal '=' 'getchar''('')'';'
        this.type = type;
        this.lValBlock = lValBlock;
        this.assignToken = assignToken;
        this.getToken = getToken;
        this.leftParentToken = leftParentToken;
        this.rightParentToken = rightParentToken;
        this.semicnToken = semicnToken;
    }


    public StmtBlock(StmtType type, Token printfToken, Token leftParentToken, Token stringConst, List<Token> commaList, List<ExpBlock> expBlockList, Token rightParentToken, Token semicnToken) {
        // 'printf' '(' FormatString { ',' Exp } ')' ';'
        this.type = type;
        this.printfToken = printfToken;
        this.leftParentToken = leftParentToken;
        this.stringConst = stringConst;
        this.commaList = commaList;
        this.expBlockList = expBlockList;
        this.rightParentToken = rightParentToken;
        this.semicnToken = semicnToken;
    }


    public void print(){

        if(type==StmtType.LValAssignExp){
            lValBlock.print();
            assignToken.printlnParser();
            expBlock.print();
            semicnToken.printlnParser();
        }else if(type == StmtType.Exp){
            if(expBlock!=null){
                expBlock.print();
            }
            semicnToken.printlnParser();
        } else if (type==StmtType.Block) {
            blockBlock.print();
        } else if (type == StmtType.If) {
            ifToken.printlnParser();
            leftParentToken.printlnParser();
            condBlock.print();
            rightParentToken.printlnParser();
            int i = 0;
            stmtBlockList.get(i).print();
            for(i=1;i<stmtBlockList.size();i++){
                elseToken.printlnParser();
                stmtBlockList.get(i).print();
            }
        } else if(type==StmtType.For){
            forToken.printlnParser();
            leftParentToken.printlnParser();
            if(forStmtBlock1!=null){
                forStmtBlock1.print();
            }
            forSemicn1.printlnParser();
            if(condBlock!=null){
                condBlock.print();
            }
            forSemicn2.printlnParser();
            if(forStmtBlock2!=null){
                forStmtBlock2.print();
            }
            rightParentToken.printlnParser();
            stmtBlock.print();
        } else if (type==StmtType.Break||type==StmtType.Continue) {
            breakOrContinueToken.printlnParser();
            semicnToken.printlnParser();
        } else if(type==StmtType.Return){
            returnToken.printlnParser();
            if(expBlock!=null){
                expBlock.print();
            }
            semicnToken.printlnParser();
        } else if (type==StmtType.GetInt || type==StmtType.GetChar) {
            lValBlock.print();
            assignToken.printlnParser();
            getToken.printlnParser();
            leftParentToken.printlnParser();
            rightParentToken.printlnParser();
            semicnToken.printlnParser();
        } else if (type == StmtType.Printf) {
            printfToken.printlnParser();
            leftParentToken.printlnParser();
            stringConst.printlnParser();
            for(int i=0;i<commaList.size();i++){
                commaList.get(i).printlnParser();
                expBlockList.get(i).print();
            }
            rightParentToken.printlnParser();
            semicnToken.printlnParser();
        }
        FileController.printlnParser(Parser.getBlockType(BlockType.Stmt));
    }
}
