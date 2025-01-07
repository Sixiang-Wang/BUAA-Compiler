package Frontend;

import Block.*;
import Symbol.*;
import Token.Token;
import Token.TokenType;
import Error.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Visitor {
    private static final Visitor visitor = new Visitor();
    public static Visitor getInstance(){
        return visitor;
    }
    private int tableId = 0;
    public List<SymbolTable> symbolTableList = new ArrayList<>();
    private int forLoop = 0;

    private void addSymbolTable(boolean isFunc,BType bType){
        int fatherId = tableId;
        tableId = symbolTableList.size()+1;

        System.out.println("\naddSymbolTable...");
        System.out.println("fatherId:"+fatherId+"\ntableId:"+tableId);

        SymbolTable symbolTable = new SymbolTable(tableId,fatherId,new LinkedHashMap<>(),isFunc,bType);
        symbolTableList.add(symbolTable);
    }
    private void removeSymbolTable(){
        tableId = symbolTableList.get(tableId-1).fatherId;
        //symbolTableList.remove(symbolTableList.size()-1);
    }
    private boolean containsInCurrent(String ident){
        //SymbolTable symbolTable = symbolTableList.get(symbolTableList.size()-1);
        SymbolTable symbolTable = symbolTableList.get(tableId-1);
        return symbolTable.directory.containsKey(ident);
    }
    private boolean containsInAll(String ident) {
        int id = tableId;
        SymbolTable symbolTable = symbolTableList.get(id-1);
        while(id>0){
            if (symbolTable.directory.containsKey(ident)) {
                return true;
            }
            id = symbolTable.fatherId;
            if(id<=0){
                return false;
            }
            symbolTable = symbolTableList.get(id-1);
        }

        return false;
    }
    private void put(String ident,Symbol symbol){
        //symbolTableList.get(symbolTableList.size()-1).directory.put(ident,symbol);
        symbolTableList.get(tableId-1).directory.put(ident,symbol);
    }
    private Symbol get(String ident){
        int id = tableId;
        SymbolTable symbolTable = symbolTableList.get(id-1);
        while(id>0) {
            if (symbolTable.directory.containsKey(ident)) {
                return symbolTable.directory.get(ident);
            }
            id = symbolTable.fatherId;
            if(id<=0){
                return null;
            }
            symbolTable = symbolTableList.get(id-1);
        }

        return null;
    }


    private Boolean inFuncDirectly(){
        return symbolTableList.get(tableId-1).isFunc;
    }
    private Boolean inFuncWhole(){
        int id = tableId;
        SymbolTable symbolTable = symbolTableList.get(id-1);
        while(id>0){
            if(symbolTable.isFunc){
                return true;
            }
            id = symbolTable.fatherId;
            symbolTable = symbolTableList.get(id-1);
        }
        return false;
    }


    private BType getFuncTypeDirectly(){
        return symbolTableList.get(tableId-1).bType;
    }
    private BType getFuncTypeWhole(){
        int id = tableId;
        SymbolTable symbolTable = symbolTableList.get(id-1);
        while(id>0){
            if(symbolTable.isFunc){
                return symbolTable.bType;
            }
            id = symbolTable.fatherId;
            symbolTable = symbolTableList.get(id-1);
        }
        return null;
    }

    public void compUnit(CompUnitBlock compUnitBlock){
        addSymbolTable(false,null);
        for(DeclBlock declBlock:compUnitBlock.declBlockList){
            decl(declBlock);
        }
        for(FuncDefBlock funcDefBlock:compUnitBlock.funcDefBlockList){
            funcDef(funcDefBlock);
        }
        mainFuncDef(compUnitBlock.mainFuncDefBlock);
    }
    private void decl(DeclBlock declBlock){
        if(declBlock.constDeclBlock !=null){
            constDecl(declBlock.constDeclBlock);
        }else {
            varDecl(declBlock.varDeclBlock);
        }
    }
    private void constDecl(ConstDeclBlock constDeclBlock){
        TokenType type = constDeclBlock.bTypeBlock.token.type;
        BType bType;
        if(type==TokenType.INTTK){
            bType = BType.INT;
        }else{
            bType = BType.CHAR;
        }
        for(ConstDefBlock constDefBlock:constDeclBlock.constDefBlockList){
            constDef(bType,constDefBlock);
        }
    }
    private void constDef(BType bType,ConstDefBlock constDefBlock){
        if(containsInCurrent(constDefBlock.ident.token)){
            ErrorHandler.add(constDefBlock.ident.line,ErrType.b);
            return;
        }
        int dim = 0;
        if(constDefBlock.constExpBlock!=null){
            dim=1;
            for (ConstExpBlock constExpBlock:constDefBlock.constInitValBlock.constExpBlockList){
                constExp(constExpBlock);
            }
        }

        Symbol symbol = new Symbol(constDefBlock.ident.token,0,dim,bType,1);
        put(constDefBlock.ident.token,symbol);
        constInitVal(constDefBlock.constInitValBlock);
    }
    private void constInitVal(ConstInitValBlock constInitValBlock){
        if(constInitValBlock.constExpBlockList.size()==1&&constInitValBlock.leftBraceToken==null){
            constExp(constInitValBlock.constExpBlockList.get(0));
        }else if(constInitValBlock.stringConst==null){
            for(ConstExpBlock constExpBlock:constInitValBlock.constExpBlockList){
                constExp(constExpBlock);
            }
        }
    }
    private void varDecl(VarDeclBlock varDeclBlock){
        TokenType type = varDeclBlock.bTypeBlock.token.type;
        BType bType;
        if(type==TokenType.INTTK){
            bType = BType.INT;
        }else{
            bType = BType.CHAR;
        }
        for(VarDefBlock varDefBlock:varDeclBlock.varDefBlockList){
            varDef(bType,varDefBlock);
        }
    }
    private void varDef(BType bType,VarDefBlock varDefBlock){
        int dim = 0;
        if(containsInCurrent(varDefBlock.ident.token)){
            ErrorHandler.add(varDefBlock.ident.line,ErrType.b);
            return;
        }
        if(varDefBlock.constExpBlock!=null){
            dim = 1;
            constExp(varDefBlock.constExpBlock);
        }
        Token ident = varDefBlock.ident;
        Symbol symbol = new Symbol(ident.token,0,dim,bType,0);
        put(varDefBlock.ident.token,symbol);
        if(varDefBlock.initValBlock!=null){
            initVal(varDefBlock.initValBlock);
        }
    }
    private void initVal(InitValBlock initValBlock){
        List<ExpBlock> expBlockList = initValBlock.expBlockList;
        if(initValBlock.leftBraceToken==null&&initValBlock.expBlockList!=null){
            if(!initValBlock.expBlockList.isEmpty()){
                exp(expBlockList.get(0));
            }
        }else if(initValBlock.expBlockList!=null){
            for(ExpBlock expBlock:expBlockList){
                exp(expBlock);
            }
        }
    }
    private void funcDef(FuncDefBlock funcDefBlock){
        //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        Token ident = funcDefBlock.ident;
        if(containsInCurrent(ident.token)){
            ErrorHandler.add(ident.line,ErrType.b);
            return;
        }
        Token type = funcDefBlock.funcTypeBlock.type;
        BType bType;
        if(type.type==TokenType.INTTK){
            bType = BType.INT;
        }else if(type.type==TokenType.CHARTK){
            bType = BType.CHAR;
        }else {
            bType = BType.VOID;
        }

        Symbol symbol = new Symbol(ident.token,1,0,bType,0);

        if(funcDefBlock.funcFParamsBlock!=null){
            for(FuncFParamBlock funcFParamBlock:funcDefBlock.funcFParamsBlock.funcFParamBlockList){
                symbol.funcParams.add(getParamSymbol(funcFParamBlock));
            }
        }

        put(ident.token,symbol);
        addSymbolTable(true,bType);
        if(funcDefBlock.funcFParamsBlock!=null){
            funcFParams(funcDefBlock.funcFParamsBlock);
        }
        block(funcDefBlock.blockBlock);
        removeSymbolTable();
    }

    private Symbol getParamSymbol(FuncFParamBlock funcFParamBlock){
        Token paramType = funcFParamBlock.bTypeBlock.token;
        BType paramBType;
        if(paramType.type==TokenType.INTTK){
            paramBType = BType.INT;
        }else if(paramType.type==TokenType.CHARTK){
            paramBType = BType.CHAR;
        }else {
            paramBType = BType.VOID;
        }
        Token paramIdent = funcFParamBlock.ident;
        int dim = 0;
        if(funcFParamBlock.leftBracket !=null){
            dim = 1;
        }
        return new Symbol(paramIdent.token,0,dim,paramBType,0);
    }
    private void mainFuncDef(MainFuncDefBlock mainFuncDefBlock){

        addSymbolTable(true,BType.INT);
        block(mainFuncDefBlock.blockBlock);
        removeSymbolTable();
    }
    private void funcFParams(FuncFParamsBlock funcFParamsBlock){
        for(FuncFParamBlock funcFParamBlock:funcFParamsBlock.funcFParamBlockList){
            funcFParam(funcFParamBlock);
        }
    }
    private void funcFParam(FuncFParamBlock funcFParamBlock){
        if(containsInCurrent(funcFParamBlock.ident.token)){
            ErrorHandler.add(funcFParamBlock.ident.line,ErrType.b);
        }
        put(funcFParamBlock.ident.token,getParamSymbol(funcFParamBlock));
    }

    private void block(BlockBlock blockBlock){
        for(BlockItemBlock blockItemBlock:blockBlock.blockItemBlockList){
            blockItem(blockItemBlock);
        }
        if(inFuncDirectly()&& getFuncTypeDirectly()!=BType.VOID){
            if(blockBlock.blockItemBlockList.isEmpty()){
                ErrorHandler.add(blockBlock.rightBraceToken.line,ErrType.g);
                return;
            }
            BlockItemBlock lastItem = blockBlock.blockItemBlockList.get(blockBlock.blockItemBlockList.size() - 1);
            if (lastItem.stmtBlock==null||lastItem.stmtBlock.type!=StmtType.Return) {
                ErrorHandler.add(blockBlock.rightBraceToken.line,ErrType.g);
            }
        }
    }
    private void blockItem(BlockItemBlock blockItemBlock){
        if(blockItemBlock.declBlock!=null){
            decl(blockItemBlock.declBlock);
        }else {
            stmt(blockItemBlock.stmtBlock);
        }
    }
    private void stmt(StmtBlock stmtBlock){
        //Stmt → LVal '=' Exp ';' // h
        //| [Exp] ';'
        //| Block
        //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
        //| 'break' ';' | 'continue' ';' // m
        //| 'return' [Exp] ';' // f
        //| LVal '=' 'getint''('')'';' // h
        //| LVal '=' 'getchar''('')'';' // h
        //| 'printf''('StringConst {','Exp}')'';' // l
        StmtType type = stmtBlock.type;

        if(type==StmtType.LValAssignExp){
            lVal(stmtBlock.lValBlock);
            Symbol ident = null;
            if(containsInAll(stmtBlock.lValBlock.ident.token)){
                ident = get(stmtBlock.lValBlock.ident.token);
            }

            if(ident!=null&&ident.isConst==1){
                ErrorHandler.add(stmtBlock.lValBlock.ident.line,ErrType.h);
            }
            exp(stmtBlock.expBlock);
        }
        else if (type==StmtType.Exp) {
            if(stmtBlock.expBlock!=null){
                exp(stmtBlock.expBlock);
            }
        }
        else if (type==StmtType.Block) {
            addSymbolTable(false,null);
            block(stmtBlock.blockBlock);
            removeSymbolTable();
        } else if (type==StmtType.If) {
            cond(stmtBlock.condBlock);
            stmt(stmtBlock.stmtBlockList.get(0));
            if(stmtBlock.elseToken!=null){
                stmt(stmtBlock.stmtBlockList.get(1));
            }
        }
        else if (type==StmtType.For) {
            if(stmtBlock.forStmtBlock1!=null){
                forStmt(stmtBlock.forStmtBlock1);
            }
            if(stmtBlock.condBlock!=null){
                cond(stmtBlock.condBlock);
            }
            if(stmtBlock.forStmtBlock2!=null){
                forStmt(stmtBlock.forStmtBlock2);
            }
            forLoop++;
            stmt(stmtBlock.stmtBlock);
            forLoop--;
        }
        else if(type==StmtType.Continue||type==StmtType.Break){
            if(forLoop<=0){
                ErrorHandler.add(stmtBlock.breakOrContinueToken.line, ErrType.m);
            }
        }
        else if (type==StmtType.Return) {
            if(inFuncWhole()){
                if (getFuncTypeWhole()==BType.VOID&&stmtBlock.expBlock!=null){
                    ErrorHandler.add(stmtBlock.returnToken.line,ErrType.f);
                }
                if(stmtBlock.expBlock!=null){
                    exp(stmtBlock.expBlock);
                }
            }
        } else if (type==StmtType.GetInt||type==StmtType.GetChar) {
            lVal(stmtBlock.lValBlock);
            Symbol ident = null;
            if(containsInAll(stmtBlock.lValBlock.ident.token)){
                ident = get(stmtBlock.lValBlock.ident.token);
            }
            if(ident!=null&&ident.isConst==1){
                ErrorHandler.add(stmtBlock.lValBlock.ident.line,ErrType.h);
            }
        } else{
            String string = stmtBlock.stringConst.token;
            int paramNum=0;
            for(int i=0;i<string.length()-1;i++){

                //TODO: %s??
                if(string.charAt(i)=='%'&&(string.charAt(i+1)=='d'||string.charAt(i+1)=='c')){
                    paramNum++;
                }
            }
            if(paramNum!=stmtBlock.expBlockList.size()){
                ErrorHandler.add(stmtBlock.printfToken.line,ErrType.l);
            }
            for(ExpBlock expBlock:stmtBlock.expBlockList){
                exp(expBlock);
            }
        }
    }
    private void forStmt(ForStmtBlock forStmtBlock){
        //ForStmt → LVal '=' Exp
        lVal(forStmtBlock.lValBlock);
        Symbol ident = null;
        if(containsInAll(forStmtBlock.lValBlock.ident.token)){
            ident = get(forStmtBlock.lValBlock.ident.token);
        }

        if(ident!=null&&ident.isConst==1){
            ErrorHandler.add(forStmtBlock.lValBlock.ident.line,ErrType.h);
        }

        exp(forStmtBlock.expBlock);
    }
    private void exp(ExpBlock expBlock){
        addExp(expBlock.addExpBlock);
    }
    private void cond(CondBlock condBlock){
        lOrExp(condBlock.lOrExpBlock);
    }
    private void lVal(LValBlock lValBlock){
        //LVal → Ident ['[' Exp ']']
        if(!containsInAll(lValBlock.ident.token)){

            ErrorHandler.add(lValBlock.ident.line,ErrType.c);
            return;
        }
        if(lValBlock.expBlock!=null){
            exp(lValBlock.expBlock);
        }
    }


    private void primaryExp(PrimaryExpBlock primaryExpBlock){
        //PrimaryExp → '(' Exp ')' | LVal | Number | Character
        if(primaryExpBlock.expBlock!=null){
            exp(primaryExpBlock.expBlock);
        } else if (primaryExpBlock.lValBlock!=null) {
            lVal(primaryExpBlock.lValBlock);
        }
    }
    private void unaryExp(UnaryExpBlock unaryExpBlock){
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // c d e
        if(unaryExpBlock.primaryExpBlock!=null){
            primaryExp(unaryExpBlock.primaryExpBlock);
        } else if(unaryExpBlock.unaryExpBlock!=null){
            unaryExp(unaryExpBlock.unaryExpBlock);
        } else {
            Token ident = unaryExpBlock.ident;
            if(!containsInAll(ident.token)){
                ErrorHandler.add(ident.line,ErrType.c);
                return;
            }
            Symbol symbol = get(ident.token);
            if(symbol==null||symbol.isFunc==0){
                ErrorHandler.add(ident.line,ErrType.e);
                return;
            }
            if(unaryExpBlock.funcRParamsBlock==null){
                if(!symbol.funcParams.isEmpty()){
                    ErrorHandler.add(ident.line,ErrType.d);
                }
            } else {
                List<Symbol> funcParams = symbol.funcParams;
                List<Symbol> funcRParams = new ArrayList<>();
                List<ExpBlock> expBlockList = unaryExpBlock.funcRParamsBlock.expBlockList;
                if(funcParams.size()!=expBlockList.size()){
                    ErrorHandler.add(ident.line,ErrType.d);
                    return;
                }

                funcRParams(unaryExpBlock.funcRParamsBlock);
                for(ExpBlock expBlock:expBlockList){
                    Symbol funcRParam = getFuncParamInExp(expBlock);
                    if(funcRParam!=null){
                        funcRParams.add(funcRParam);
                    }

                }

                if(funcParams.size()!=funcRParams.size()) {
                    ErrorHandler.add(ident.line, ErrType.d);
                    return;
                }
                System.out.println("======funcParamStart=======");
                for(int i=0;i<funcParams.size();i++){
                    Symbol param1 = funcParams.get(i);

                    Symbol param2 = funcRParams.get(i);
                    System.out.println(param1.toString()+" "+param2.toString());
                    if(param1.dimension!=param2.dimension ||
                            (param1.dimension==1&&param1.bType!=param2.bType)){
                        ErrorHandler.add(ident.line,ErrType.e);
                        return;
                    }
                }
                System.out.println("=======funcParamEnd=======");
            }
        }
    }

    private Symbol getFuncParamInExp(ExpBlock expBlock){
        return getFuncParamInUnaryExp(expBlock.addExpBlock.mulExpBlock.unaryExpBlock);
    }
    private Symbol getFuncParamInUnaryExp(UnaryExpBlock unaryExpBlock){
        if(unaryExpBlock.primaryExpBlock!=null){
            return getFuncParamInPrimaryExp(unaryExpBlock.primaryExpBlock);
        }else if(unaryExpBlock.unaryExpBlock!=null){
            return getFuncParamInUnaryExp(unaryExpBlock.unaryExpBlock);
        }else {
            Symbol symbol = get(unaryExpBlock.ident.token);
            if(symbol==null||symbol.isFunc==0){
                System.out.println("getFuncParamInUnaryExp Error:Not Func Ident");
                ErrorHandler.add(unaryExpBlock.ident.line,ErrType.c);
                return null;
            }

            return symbol;
        }
    }

    private Symbol getFuncParamInPrimaryExp(PrimaryExpBlock primaryExpBlock){
        if(primaryExpBlock.expBlock!=null){
            return getFuncParamInExp(primaryExpBlock.expBlock);
        } else if (primaryExpBlock.lValBlock!=null) {
            return getFuncParamInLVal(primaryExpBlock.lValBlock);
        } else if(primaryExpBlock.numberBlock!=null){
            return new Symbol(null,0,0,BType.INT,1);
        } else {
            return new Symbol(null,0,0,BType.CHAR,1);
        }
    }
    private Symbol getFuncParamInLVal(LValBlock lValBlock) {
        Token ident = lValBlock.ident;
        Symbol symbol = get(ident.token);
        if(symbol==null){
            System.out.println("getFuncParamInLVal: No Symbol");
            return null;
        }

        Symbol result = new Symbol(symbol.token, symbol.isFunc,symbol.dimension,symbol.bType,symbol.isConst,symbol.funcParams);
        if(lValBlock.leftBracket!=null){
            result.dimension=0;
        }
        return result;
    }
    private void funcRParams(FuncRParamsBlock funcRParamsBlock){
        for(ExpBlock expBlock: funcRParamsBlock.expBlockList){
            exp(expBlock);
        }
    }
    private void mulExp(MulExpBlock mulExpBlock) {
        unaryExp(mulExpBlock.unaryExpBlock);
        if(mulExpBlock.mulExpBlock!=null){
            mulExp(mulExpBlock.mulExpBlock);
        }
    }
    private void addExp(AddExpBlock addExpBlock) {
        mulExp(addExpBlock.mulExpBlock);
        if(addExpBlock.addExpBlock!=null){
            addExp(addExpBlock.addExpBlock);
        }
    }
    private void relExp(RelExpBlock relExpBlock) {
        addExp(relExpBlock.addExpBlock);
        if (relExpBlock.relExpBlock != null) {
            relExp(relExpBlock.relExpBlock);
        }
    }
    private void eqExp(EqExpBlock eqExpBlock) {
        relExp(eqExpBlock.relExpBlock);
        if (eqExpBlock.eqExpBlock != null) {
            eqExp(eqExpBlock.eqExpBlock);
        }
    }
    private void lAndExp(LAndExpBlock lAndExpBlock) {
        eqExp(lAndExpBlock.eqExpBlock);
        if (lAndExpBlock.lAndExpBlock != null) {
            lAndExp(lAndExpBlock.lAndExpBlock);
        }
    }
    private void lOrExp(LOrExpBlock lOrExpBlock) {
        lAndExp(lOrExpBlock.lAndExpBlock);
        if (lOrExpBlock.lOrExpBlock != null) {
            lOrExp(lOrExpBlock.lOrExpBlock);
        }
    }
    private void constExp(ConstExpBlock constExpBlock) {
        addExp(constExpBlock.addExpBlock);
    }

    public void print(){
        for(SymbolTable symbolTable:symbolTableList){
            symbolTable.print();
        }
    }
}
