package Middle;

import Block.*;
import Middle.Types.*;
import Middle.Values.*;
import Middle.Values.ConstArray;
import Middle.Values.instructions.Operator;
import Symbol.*;
import Token.Token;
import Token.TokenType;

import java.util.*;

public class IrBuilder {
    public CompUnitBlock compUnitBlock;

    private final BuildFactory buildFactory = BuildFactory.getInstance();


    /**
     *  符号表
     */
    SymbolTableList symbolTableList = new SymbolTableList();
    /**
     * 需要在语法树上向上/向下传递的变量
     */
    private BasicBlock curBlock = null;
    private BasicBlock curTrueBlock = null;
    private BasicBlock curFalseBlock = null;
    private BasicBlock continueBlock = null;
    private BasicBlock curForFinalBlock = null;
    private Function curFunction = null;
    private Integer saveValue = null;
    private Operator saveOp = null;
    private int tmpIndex = 0;
    private Operator tmpOp = null;
    private Type tmpType = null;
    private Value tmpValue = null;
    private List<Value> tmpList = null;
    private List<Type> tmpTypeList = null;
    private List<Value> funcArgsList = null;
    private boolean isGlobal = true;
    private boolean isConst = false;
    private boolean isArray = false;
    private boolean paramAddSymbol = false;
    private Value curArray = null;
    private String tmpName = null;
    private int tmpOffset = 0;
    private List<Integer> tmpDims = null;

    private int calculate(Operator op,int a,int b){
        switch (op){
            case Add -> {
                return a+b;
            }
            case Sub -> {
                return a-b;
            }
            case Mul->{
                return a * b;
            }
            case Div->{
                return a / b;
            }
            case Mod->{
                return a % b;
            }
            default->{
                System.out.println("In IRBuilder calculate(): 这...对吗?");
                return 0;
            }
        }
    }

    /**
     * printf("...")字符串处理
     */
    private final Map<String,String> stringMap = new HashMap<>();
    private int stringIdCounter = 0;
    public Value getStringConst(String string){
        if(stringMap.get(string)!=null){
            return symbolTableList.getGlobalValue(stringMap.get(string));
        }
        Type type = buildFactory.getArrayType(IntegerType.i8,string.length()+1);
        int id = stringIdCounter++;
        String name = "_str_ciallo_"+id;
        Value value =buildFactory.buildGlobalVar(name, type, true, buildFactory.getConstString(string));
        stringMap.put(string,name);
        symbolTableList.addGlobalSymbol(name,value);
        return value;
    }

    public IrBuilder(CompUnitBlock compUnitBlock){
        this.compUnitBlock = compUnitBlock;
    }


    public void visitCompUnit(CompUnitBlock compUnitBlock){
        isGlobal = true;
        symbolTableList.addSymbolTable();
        symbolTableList.addSymbol("getint",buildFactory.buildLibraryFunction("getint", IntegerType.i32, new ArrayList<>()));
        symbolTableList.addSymbol("getchar",buildFactory.buildLibraryFunction("getchar", IntegerType.i32, new ArrayList<>()));
        symbolTableList.addSymbol("putint", buildFactory.buildLibraryFunction("putint", VoidType.voidType, new ArrayList<>(Collections.singleton(IntegerType.i32))));
        symbolTableList.addSymbol("putch", buildFactory.buildLibraryFunction("putch", VoidType.voidType, new ArrayList<>(Collections.singleton(IntegerType.i32))));
        symbolTableList.addSymbol("putstr", buildFactory.buildLibraryFunction("putstr", VoidType.voidType, new ArrayList<>(Collections.singleton(new PointerType(IntegerType.i8)))));


        for(DeclBlock declBlock:compUnitBlock.declBlockList){
            visitDecl(declBlock);
        }
        for(FuncDefBlock funcDefBlock:compUnitBlock.funcDefBlockList){
            visitFuncDef(funcDefBlock);
        }
        visitMainFuncDef(compUnitBlock.mainFuncDefBlock);
    }
    private void visitDecl(DeclBlock declBlock){
        if(declBlock.constDeclBlock !=null){
            visitConstDecl(declBlock.constDeclBlock);
        }else {
            visitVarDecl(declBlock.varDeclBlock);
        }
    }
    private void visitConstDecl(ConstDeclBlock constDeclBlock){
        TokenType type = constDeclBlock.bTypeBlock.token.type;
        if(type==TokenType.INTTK){
            tmpType = IntegerType.i32;
        }else{
            tmpType = IntegerType.i8;
        }
        for(ConstDefBlock constDefBlock:constDeclBlock.constDefBlockList){
            visitConstDef(constDefBlock);
        }
    }

    private void visitConstDef(ConstDefBlock constDefBlock){
        String name = constDefBlock.ident.token;
        if(constDefBlock.constExpBlock==null){
            tmpValue = null;
            visitConstInitVal(constDefBlock.constInitValBlock);
            if(tmpValue==null){
                tmpValue = buildFactory.getConstInt(saveValue==null?0:saveValue,tmpType);
            }
            symbolTableList.addConst(name,saveValue);

            if(isGlobal){
                tmpValue = buildFactory.buildGlobalVar(name,tmpType,true,tmpValue);
            }else {
                tmpValue = buildFactory.buildVar(curBlock,tmpValue,true,tmpType);
            }
            symbolTableList.addSymbol(name,tmpValue);
        }else {
            Integer size;
            visitConstExp(constDefBlock.constExpBlock);
            size = saveValue;
            tmpDims = new ArrayList<>();
            tmpDims.add(size);
            Type type = buildFactory.getArrayType(tmpType,size);
            if(isGlobal){
                tmpValue = buildFactory.buildGlobalArray(name,type,true);
                ((ConstArray)(((GlobalVar)tmpValue).getValue())).setInit(true);
            }else {
                tmpValue = buildFactory.buildArray(curBlock,true,type);
            }
            symbolTableList.addSymbol(name,tmpValue);
            curArray = tmpValue;
            isArray = true;
            tmpName = name;
            tmpOffset = 0;
            visitConstInitVal(constDefBlock.constInitValBlock);
            isArray = false;
        }
    }
    private void visitConstInitVal(ConstInitValBlock constInitValBlock){
        if(constInitValBlock.constExpBlockList.size()==1&&constInitValBlock.leftBraceToken==null&&!isArray){
            visitConstExp(constInitValBlock.constExpBlockList.get(0));
        }else if(constInitValBlock.stringConst==null){
            for(ConstExpBlock constExpBlock:constInitValBlock.constExpBlockList){
                tmpValue = null;
                visitConstExp(constExpBlock);
                if(tmpValue==null){
                    tmpValue = buildFactory.getConstInt(saveValue,tmpType);
                }
                if(isGlobal){
                    buildFactory.buildInitArray(curArray,tmpOffset,tmpValue);
                }else {
                    buildFactory.buildStore(curBlock,buildFactory.buildGEP(curBlock,curArray,tmpOffset),tmpValue);
                }
                List<Value> index = ((ArrayType)((PointerType) curArray.getType()).getTargetType()).offset2Index(tmpOffset);
                StringBuilder name = new StringBuilder(tmpName);
                for(Value value:index){
                    name.append(((ConstInt) value).getValue()).append(";");
                }
                symbolTableList.addConst(name.toString(), saveValue);
                tmpOffset++;
            }
        }else {
            String stringConst = constInitValBlock.stringConst.token;
            for(int i=1;i<stringConst.length()-1;i++){
                int charInt = stringConst.charAt(i);
                tmpValue = buildFactory.getConstInt(charInt,tmpType);
                if(isGlobal){
                    buildFactory.buildInitArray(curArray,tmpOffset,tmpValue);
                }else {
                    buildFactory.buildStore(curBlock,buildFactory.buildGEP(curBlock,curArray,tmpOffset),tmpValue);
                }
                List<Value> index = ((ArrayType)((PointerType) curArray.getType()).getTargetType()).offset2Index(tmpOffset);
                StringBuilder name = new StringBuilder(tmpName);
                for(Value value:index){
                    name.append(((ConstInt) value).getValue()).append(";");
                }
                symbolTableList.addConst(name.toString(), charInt);
                tmpOffset++;
            }

        }
    }
    private void visitVarDecl(VarDeclBlock varDeclBlock){
        TokenType type = varDeclBlock.bTypeBlock.token.type;
        if(type==TokenType.INTTK){
            tmpType = IntegerType.i32;
        }else{
            tmpType = IntegerType.i8;
        }
        for(VarDefBlock varDefBlock:varDeclBlock.varDefBlockList){
            visitVarDef(varDefBlock);
        }
    }
    private void visitVarDef(VarDefBlock varDefBlock){
        String name = varDefBlock.ident.token;
        if(varDefBlock.constExpBlock==null){
            if(varDefBlock.initValBlock!=null){
                if(isGlobal){
                    isConst = true;
                    saveValue = null;
                }
                tmpValue = null;
                visitInitVal(varDefBlock.initValBlock);
                isConst = false;
            }else {
                tmpValue = null;
                if(isGlobal){
                    saveValue = null;
                }
            }

            if(isGlobal){
                tmpValue = buildFactory.buildGlobalVar(name,
                        tmpType,
                        false,
                        buildFactory.getConstInt(saveValue == null ? 0 : saveValue, tmpType));
            } else {
                tmpValue = buildFactory.buildVar(curBlock, tmpValue, isConst, tmpType);
            }
            symbolTableList.addSymbol(name,tmpValue);
        } else {
            isConst = true;
            visitConstExp(varDefBlock.constExpBlock);
            isConst = false;
            tmpDims = new ArrayList<>();
            tmpDims.add(saveValue);
            Type type = buildFactory.getArrayType(tmpType, saveValue);
            if(isGlobal){
                tmpValue = buildFactory.buildGlobalArray(name, type, false);
                if (varDefBlock.initValBlock != null) {
                    ((ConstArray) ((GlobalVar) tmpValue).getValue()).setInit(true);
                }
            } else {
                tmpValue = buildFactory.buildArray(curBlock, false, type);
            }
            symbolTableList.addSymbol(name,tmpValue);
            //向下传递参数
            curArray = tmpValue;

            if(varDefBlock.initValBlock!=null){
                isArray = true;
                tmpName = name;
                tmpOffset = 0;
                visitInitVal(varDefBlock.initValBlock);
                isArray = false;
            }
            isConst = false;
        }
    }
    private void visitInitVal(InitValBlock initValBlock){
        List<ExpBlock> expBlockList = initValBlock.expBlockList;
        if(initValBlock.leftBraceToken==null&&expBlockList!=null&&!expBlockList.isEmpty()){
            visitExp(expBlockList.get(0));
        }else if(initValBlock.leftBraceToken!=null){
            for(ExpBlock expBlock:expBlockList){
                if(isGlobal){
                    isConst = true;
                }
                saveValue = null;
                tmpValue = null;
                visitExp(expBlock);
                isConst = false;
                if (isGlobal) {
                    tmpValue = buildFactory.getConstInt(saveValue,tmpType);
                    buildFactory.buildInitArray(curArray, tmpOffset, tmpValue);
                } else {
                    tmpValue.setType(tmpType);
                    buildFactory.buildStore(curBlock, buildFactory.buildGEP(curBlock, curArray, tmpOffset), tmpValue);
                }
                tmpOffset++;
            }

            if(!isGlobal&&tmpType==IntegerType.i8&&tmpOffset<tmpDims.get(0)){
                tmpValue = new ConstInt(0);
                tmpValue.setType(tmpType);
                buildFactory.buildStore(curBlock, buildFactory.buildGEP(curBlock, curArray, tmpOffset), tmpValue);
            }
        }else {
            String stringConst = initValBlock.stringConst.token;
            for(int i=1;i<stringConst.length()-1;i++){
                saveValue = (int) stringConst.charAt(i);
                int charInt = stringConst.charAt(i);
                tmpValue = buildFactory.getConstInt(charInt,tmpType);
                if (isGlobal) {
                    tmpValue = buildFactory.getConstInt(saveValue,tmpType);
                    buildFactory.buildInitArray(curArray, tmpOffset, tmpValue);
                } else {
                    tmpValue.setType(tmpType);
                    buildFactory.buildStore(curBlock, buildFactory.buildGEP(curBlock, curArray, tmpOffset), tmpValue);
                }
                tmpOffset++;
            }

            if(!isGlobal&&tmpOffset<tmpDims.get(0)){
                tmpValue = new ConstInt(0);
                tmpValue.setType(tmpType);
                buildFactory.buildStore(curBlock, buildFactory.buildGEP(curBlock, curArray, tmpOffset), tmpValue);
            }
        }
    }
    private void visitFuncDef(FuncDefBlock funcDefBlock){
        //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        Token ident = funcDefBlock.ident;
        String funcName = ident.token;
        TokenType tokenType = funcDefBlock.funcTypeBlock.type.type;
        Type funcType;
        if(tokenType==TokenType.INTTK){
            funcType = IntegerType.i32;
        }else if(tokenType == TokenType.CHARTK){
            funcType = IntegerType.i8;
        }else {
            funcType = VoidType.voidType;
        }
        tmpTypeList = new ArrayList<>();
        //1. Generate Value for Function
        paramAddSymbol = false;
        if(funcDefBlock.funcFParamsBlock!=null){
            visitFuncFParams(funcDefBlock.funcFParamsBlock);
        }
        Function function = buildFactory.buildFunction(funcName, funcType, tmpTypeList);
        curFunction = function;
        symbolTableList.addSymbol(funcName,function);
        symbolTableList.addSymbolTable();
        symbolTableList.addSymbol(funcName,function);

        curBlock = buildFactory.buildBasicBlock(curFunction);
        funcArgsList = buildFactory.getFunctionArguments(curFunction);

        //2. Generate Symbol for SymbolTable
        paramAddSymbol = true;
        if(funcDefBlock.funcFParamsBlock!=null){
            visitFuncFParams(funcDefBlock.funcFParamsBlock);
        }
        paramAddSymbol = false;

        visitBlock(funcDefBlock.blockBlock);
        isGlobal = true;
        symbolTableList.removeSymbolTable();
        buildFactory.checkFuncRet(curBlock);
    }

    private void visitMainFuncDef(MainFuncDefBlock mainFuncDefBlock){
        isGlobal = false;
        Function function = buildFactory.buildFunction("main", IntegerType.i32, new ArrayList<>());
        curFunction = function;
        symbolTableList.addSymbol("main", function);
        symbolTableList.addSymbolTable();
        symbolTableList.addSymbol("main", function);
        curBlock = buildFactory.buildBasicBlock(curFunction);
        funcArgsList = buildFactory.getFunctionArguments(curFunction);
        visitBlock(mainFuncDefBlock.blockBlock);
        isGlobal = true;
        symbolTableList.removeSymbolTable();
        buildFactory.checkFuncRet(curBlock);
    }
    private void visitFuncFParams(FuncFParamsBlock funcFParamsBlock){
        if(paramAddSymbol){
            tmpIndex = 0;
            for(FuncFParamBlock funcFParamBlock:funcFParamsBlock.funcFParamBlockList){
                visitFuncFParam(funcFParamBlock);
                tmpIndex++;
            }
        }else {
            tmpTypeList = new ArrayList<>();
            for(FuncFParamBlock funcFParamBlock:funcFParamsBlock.funcFParamBlockList){
                visitFuncFParam(funcFParamBlock);
                tmpTypeList.add(tmpType);
            }
        }
    }
    private void visitFuncFParam(FuncFParamBlock funcFParamBlock){
        TokenType tokenType = funcFParamBlock.bTypeBlock.token.type;
        IntegerType type;
        if(tokenType==TokenType.CHARTK){
            type = IntegerType.i8;
        } else{
            type = IntegerType.i32;
        }
        if(paramAddSymbol){
            String name = funcFParamBlock.ident.token;
            Value value = buildFactory.buildVar(curBlock,
                    funcArgsList.get(tmpIndex),
                    false,
                    tmpTypeList.get(tmpIndex));
            symbolTableList.addSymbol(name,value);
        }else {
            if(funcFParamBlock.leftBracket ==null){
                tmpType = type;
            } else {
                tmpType = buildFactory.getArrayType(type,-1);
            }
        }
    }

    private void visitBlock(BlockBlock blockBlock){
        for(BlockItemBlock blockItemBlock:blockBlock.blockItemBlockList){
            visitBlockItem(blockItemBlock);
        }
    }
    private void visitBlockItem(BlockItemBlock blockItemBlock){
        if(blockItemBlock.declBlock!=null){
            isGlobal = false;
            visitDecl(blockItemBlock.declBlock);
        }else {
            visitStmt(blockItemBlock.stmtBlock);
        }
    }
    private void visitStmt(StmtBlock stmtBlock){
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
            Value lVal;
            if(stmtBlock.lValBlock.expBlock==null){
                //IntegerType
                lVal = symbolTableList.getValue(stmtBlock.lValBlock.ident.token);
                visitExp(stmtBlock.expBlock);
            } else {

                visitExp(stmtBlock.lValBlock.expBlock);
                Value indexValue = tmpValue;
                List<Value> indexList = new ArrayList<>();

                tmpValue = symbolTableList.getValue(stmtBlock.lValBlock.ident.token);
                Type tmpValueType = tmpValue.getType();
                Type targetType =  ((PointerType) tmpValueType).getTargetType();
                if (targetType instanceof PointerType) {
                    tmpValue = buildFactory.buildLoad(curBlock, tmpValue);
                } else {
                    indexList.add(ConstInt.ZERO);
                }

                indexList.add(indexValue);
                lVal = buildFactory.buildGEP(curBlock,tmpValue,indexList);
                visitExp(stmtBlock.expBlock);
            }
            tmpValue = buildFactory.buildStore(curBlock,lVal,tmpValue);
        }
        else if (type==StmtType.Exp) {
            if(stmtBlock.expBlock!=null){
                visitExp(stmtBlock.expBlock);
            }
        }
        else if (type==StmtType.Block) {
            symbolTableList.addSymbolTable();
            visitBlock(stmtBlock.blockBlock);
            symbolTableList.removeSymbolTable();
        } else if (type==StmtType.If) {
        // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            if (stmtBlock.elseToken == null) {
                //if(CondBlock){trueBlock;} finalBlock;
                BasicBlock curBlockTmp = curBlock;
                BasicBlock trueBlock = buildFactory.buildBasicBlock(curFunction);
                curBlock = trueBlock;
                visitStmt(stmtBlock.stmtBlockList.get(0));
                BasicBlock finalBlock = buildFactory.buildBasicBlock(curFunction);
                buildFactory.buildBr(curBlock,finalBlock);

                curTrueBlock = trueBlock;
                curFalseBlock = finalBlock;
                curBlock = curBlockTmp;
                visitCond(stmtBlock.condBlock);
                curBlock = finalBlock;
            }
            else {
                BasicBlock curBlockTmp = curBlock;
                BasicBlock trueBlock = buildFactory.buildBasicBlock(curFunction);
                curBlock = trueBlock;
                visitStmt(stmtBlock.stmtBlockList.get(0));
                BasicBlock trueFinalBlock = curBlock;


                BasicBlock falseBlock = buildFactory.buildBasicBlock(curFunction);
                curBlock = falseBlock;
                visitStmt(stmtBlock.stmtBlockList.get(1));
                BasicBlock falseFinalBlock = curBlock;

                curBlock = curBlockTmp;
                curTrueBlock = trueBlock;
                curFalseBlock = falseBlock;
                visitCond(stmtBlock.condBlock);

                BasicBlock finalBlock = buildFactory.buildBasicBlock(curFunction);
                buildFactory.buildBr(trueFinalBlock,finalBlock);
                buildFactory.buildBr(falseFinalBlock,finalBlock);
                curBlock = finalBlock;
            }
        }
        else if (type==StmtType.For) {
//=============================================================
//      'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt |
//          forStmt1;                                         |
//          condBlock1:                                       |
//              br cond, forBlock, finalBlock;                |
//          forBlock;                                         |
//          br condBlock2;                                    |
//          condBlock2:                                       |
//              forStmt2;                                     |
//              br cond, forBlock, finalBlock;                |
//          finalBlock;                                       |
//=============================================================
            BasicBlock outsideContinueBlock = continueBlock;
            BasicBlock outsideFinalBlock = curForFinalBlock;
            BasicBlock outsideCurBlock = curBlock;
            if(stmtBlock.forStmtBlock1!=null){
                visitForStmt(stmtBlock.forStmtBlock1);
            }


            BasicBlock forBlock = buildFactory.buildBasicBlock(curFunction);
            curBlock = forBlock;

            BasicBlock condBlock = buildFactory.buildBasicBlock(curFunction);
            continueBlock = condBlock;

            BasicBlock finalBlock = buildFactory.buildBasicBlock(curFunction);
            curForFinalBlock = finalBlock;

            visitStmt(stmtBlock.stmtBlock);

            buildFactory.buildBr(curBlock,condBlock);

            continueBlock = outsideContinueBlock;
            curForFinalBlock = outsideFinalBlock;

            curTrueBlock = forBlock;
            curFalseBlock = finalBlock;
            curBlock = outsideCurBlock;
            if(stmtBlock.condBlock!=null){
                visitCond(stmtBlock.condBlock);
            }else{
                buildFactory.buildBr(curBlock,forBlock);
            }


            curTrueBlock = forBlock;
            curFalseBlock = finalBlock;
            curBlock = condBlock;
            if(stmtBlock.forStmtBlock2!=null){
                visitForStmt(stmtBlock.forStmtBlock2);
            }
            if(stmtBlock.condBlock!=null){
                visitCond(stmtBlock.condBlock);
            }else {
                buildFactory.buildBr(curBlock,forBlock);
            }

            curBlock = finalBlock;
        }
        else if(type==StmtType.Continue){
            buildFactory.buildBr(curBlock,continueBlock);
        } else if (type==StmtType.Break) {
            buildFactory.buildBr(curBlock, curForFinalBlock);
        } else if (type==StmtType.Return) {
            if(stmtBlock.expBlock==null){
                buildFactory.buildRet(curBlock);
            }else {
                visitExp(stmtBlock.expBlock);
                buildFactory.buildRet(curBlock,tmpValue);
            }
        } else if (type==StmtType.GetInt||type==StmtType.GetChar) {
//          LVal '=' 'getXXX''('')'';'
            Value lVal;
            Function getFunc;
            if(type==StmtType.GetInt){
                getFunc = (Function) symbolTableList.getValue("getint");
            }else {
                getFunc = (Function) symbolTableList.getValue("getchar");
            }
            if(stmtBlock.lValBlock.expBlock==null){
                //IntegerType
                lVal = symbolTableList.getValue(stmtBlock.lValBlock.ident.token);
            } else {
                visitExp(stmtBlock.lValBlock.expBlock);
                Value indexValue = tmpValue;
                List<Value> indexList = new ArrayList<>();

                tmpValue = symbolTableList.getValue(stmtBlock.lValBlock.ident.token);
                Type tmpValueType = tmpValue.getType();
                Type targetType =  ((PointerType) tmpValueType).getTargetType();
                if (targetType instanceof PointerType) {
                    tmpValue = buildFactory.buildLoad(curBlock, tmpValue);
                } else {
                    indexList.add(ConstInt.ZERO);
                }

                indexList.add(indexValue);
                lVal = buildFactory.buildGEP(curBlock,tmpValue,indexList);
            }
            tmpValue = buildFactory.buildCall(curBlock,getFunc,new ArrayList<>());
            tmpValue = buildFactory.buildStore(curBlock,lVal,tmpValue);
        } else{
//          'printf''('StringConst {','Exp}')'';'
            String stringConst = stmtBlock.stringConst.token;
            List<String> stringList = cutString(stringConst);
            LinkedList<Value> values = new LinkedList<>();
            List<Value> formantValues = new ArrayList<>();
            for(ExpBlock expBlock:stmtBlock.expBlockList){
                visitExp(expBlock);
                values.add(tmpValue);
            }
            for(String format:stringList){
                if("%d".equals(format)){
                    formantValues.add(values.removeFirst());
                    buildFactory.buildCall(curBlock,(Function) symbolTableList.getValue("putint"),formantValues);
                    formantValues.remove(0);
                } else if ("%c".equals(format)) {
                    formantValues.add(values.removeFirst());
                    buildFactory.buildCall(curBlock,(Function) symbolTableList.getValue("putch"),formantValues);
                    formantValues.remove(0);
                }else {
                    List<Value> index = new ArrayList<>();
                    index.add(ConstInt.ZERO);
                    index.add(ConstInt.ZERO);
                    Value strCon = buildFactory.buildGEP(curBlock,getStringConst(format),index);
                    index = new ArrayList<>();
                    index.add(strCon);
                    buildFactory.buildCall(curBlock,(Function)symbolTableList.getValue("putstr"),index);
                }
            }
        }
    }


    List<String> cutString(String stringConst){
        List<String> strList = new ArrayList<>();
        int i;
        StringBuilder tmpStr = new StringBuilder();
        for(i=0;i<stringConst.length();i++){
            if(stringConst.charAt(i)=='\"'){
                continue;
            }
            if(stringConst.charAt(i)=='%'){
                if(stringConst.charAt(i+1)=='d'){
                    strList.add(tmpStr.toString());
                    tmpStr = new StringBuilder();
                    strList.add("%d");
                    i++;
                    continue;
                }else if(stringConst.charAt(i+1)=='c'){
                    strList.add(tmpStr.toString());
                    tmpStr = new StringBuilder();
                    strList.add("%c");
                    i++;
                    continue;
                }
            }
            if(stringConst.charAt(i)=='\\'){
                if(stringConst.charAt(i+1)=='n'){
                    tmpStr.append('\n');
                    i++;
                    continue;
                }
            }
            tmpStr.append(stringConst.charAt(i));
        }
        if(!tmpStr.isEmpty()){
            strList.add(tmpStr.toString());
        }
        return strList;
    }

    private void visitForStmt(ForStmtBlock forStmtBlock){
        //ForStmt → LVal '=' Exp
        Value lVal;
        if(forStmtBlock.lValBlock.expBlock==null){
            //IntegerType
            lVal = symbolTableList.getValue(forStmtBlock.lValBlock.ident.token);
            visitExp(forStmtBlock.expBlock);
        } else {
            visitExp(forStmtBlock.lValBlock.expBlock);
            List<Value> indexList = new ArrayList<>();
            indexList.add(ConstInt.ZERO);
            indexList.add(tmpValue);
            tmpValue = symbolTableList.getValue(forStmtBlock.lValBlock.ident.token);
            lVal = buildFactory.buildGEP(curBlock,tmpValue,indexList);
            visitExp(forStmtBlock.expBlock);
        }
        tmpValue = buildFactory.buildStore(curBlock,lVal,tmpValue);
    }

    private void visitExp(ExpBlock expBlock){
        tmpValue = null;
        saveValue = null;
        visitAddExp(expBlock.addExpBlock);
    }
    private void visitCond(CondBlock condBlock){
        visitLOrExp(condBlock.lOrExpBlock);
    }
    private void visitLVal(LValBlock lValBlock){
        //LVal → Ident ['[' Exp ']']
        if(isConst){
            StringBuilder tmpName = new StringBuilder();
            tmpName.append(lValBlock.ident.token);
            if(lValBlock.expBlock!=null){
                //用字符串找哈哈
                tmpName.append("0;");
                visitExp(lValBlock.expBlock);
                int constIntValue = saveValue==null?0:saveValue;
                tmpName.append(buildFactory.getConstInt(constIntValue).value).append(";");
            }
            saveValue = symbolTableList.getConst(tmpName.toString());
        } else {
            if(lValBlock.expBlock==null){
                tmpValue = symbolTableList.getValue(lValBlock.ident.token);
                Type type = tmpValue.getType();
                //1.Not Array Pointer
                if(!(((PointerType) type).getTargetType() instanceof ArrayType)){
                    tmpValue = buildFactory.buildLoad(curBlock, tmpValue);
                }
                //2.Is Array Pointer
                else {
                    List<Value> indexList = new ArrayList<>();
                    indexList.add(ConstInt.ZERO);
                    indexList.add(ConstInt.ZERO);
                    tmpValue = buildFactory.buildGEP(curBlock, tmpValue, indexList);
                }
            }
            else {
            /*
                array[x]
                %1 = getelementptr [5 x i32], [5 x i32]* @a, i32 0, i32 3
            */
                List<Value> indexList = new ArrayList<>();
                visitExp(lValBlock.expBlock);
                indexList.add(tmpValue);

                tmpValue = symbolTableList.getValue(lValBlock.ident.token);
                Type type = tmpValue.getType();
                Type targetType = ((PointerType) type).getTargetType();
                if (targetType instanceof PointerType) {
                    tmpValue = buildFactory.buildLoad(curBlock, tmpValue);
                } else {
                    indexList.add(0, ConstInt.ZERO);
                }
                Value tmp = buildFactory.buildGEP(curBlock, tmpValue, indexList);
                tmpValue = buildFactory.buildLoad(curBlock, tmp);
            }
        }
    }


    private void visitPrimaryExp(PrimaryExpBlock primaryExpBlock){
        //PrimaryExp → '(' Exp ')' | LVal | Number | Character
        if(primaryExpBlock.expBlock!=null){
            visitExp(primaryExpBlock.expBlock);
        } else if (primaryExpBlock.lValBlock!=null) {
            visitLVal(primaryExpBlock.lValBlock);
        } else if (primaryExpBlock.numberBlock!=null) {
            visitNumber(primaryExpBlock.numberBlock);
        }else {
            visitCharacter(primaryExpBlock.characterBlock);
        }
    }
    private void visitNumber(NumberBlock numberBlock){
        int n = Integer.parseInt(numberBlock.token.token);
        if (isConst) {
            saveValue = n;
        } else {
            tmpValue = buildFactory.getConstInt(n);
        }
    }
    private final Map<Character,Integer> charMap = new HashMap<>(){{
        put('a',7);
        put('b',8);
        put('t',9);
        put('n',10);
        put('v',11);
        put('f',12);
        put('\"',34);
        put('\'',39);
        put('\\',92);
        put('0',0);
    }};
    private void visitCharacter(CharacterBlock characterBlock){

        int c = characterBlock.token.token.charAt(1);
        if(c == '\\' ){
            char tmpC = characterBlock.token.token.charAt(2);
            if(charMap.containsKey(tmpC)){
                c = charMap.get(tmpC);
            }
        }
        if (isConst) {
            saveValue = c;
        } else {
            tmpValue = buildFactory.getConstInt(c,IntegerType.i8);
        }
    }
    private void visitUnaryExp(UnaryExpBlock unaryExpBlock){
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // c d e
        if(unaryExpBlock.primaryExpBlock!=null){
            visitPrimaryExp(unaryExpBlock.primaryExpBlock);
        } else if(unaryExpBlock.ident != null){
            tmpList = new ArrayList<>();
            if(unaryExpBlock.funcRParamsBlock!=null){
                visitFuncRParams(unaryExpBlock.funcRParamsBlock);
            }
            tmpValue = buildFactory.buildCall(curBlock,
                    (Function) symbolTableList.getValue(unaryExpBlock.ident.token),
                    tmpList);
        } else {
            visitUnaryExp(unaryExpBlock.unaryExpBlock);
            if(unaryExpBlock.unaryOpBlock.op.type==TokenType.MINU){
                if(isConst){
                    saveValue = - saveValue;
                }else {
                    tmpValue = buildFactory.buildBinary(curBlock, Operator.Sub, ConstInt.ZERO, tmpValue);
                }
            }else if(unaryExpBlock.unaryOpBlock.op.type!=TokenType.PLUS){
                tmpValue = buildFactory.buildNot(curBlock,tmpValue);
            }
        }
    }


    private void visitFuncRParams(FuncRParamsBlock funcRParamsBlock){
        List<Value> args = new ArrayList<>();
        for(ExpBlock expBlock: funcRParamsBlock.expBlockList){
            visitExp(expBlock);
            args.add(tmpValue);
        }
        tmpList = args;
    }
    private void visitMulExp(MulExpBlock mulExpBlock) {
        if(isConst){
            Integer value = saveValue;
            Operator op = saveOp;
            saveValue = null;
            visitUnaryExp(mulExpBlock.unaryExpBlock);
            if(value!=null){
                saveValue = calculate(op,value,saveValue);
            }
            if(mulExpBlock.mulExpBlock!=null){
                if(mulExpBlock.op.type==TokenType.MULT){
                    saveOp = Operator.Mul;
                } else if(mulExpBlock.op.type==TokenType.DIV){
                    saveOp = Operator.Div;
                } else if(mulExpBlock.op.type==TokenType.MOD){
                    saveOp = Operator.Mod;
                } else {
                    System.out.println("Error In IrBuilder: MulExp Error");
                }
                visitMulExp(mulExpBlock.mulExpBlock);
            }
        }
        else {
            //代替传参
            Operator op = tmpOp;
            Value value = tmpValue;

            //递归下降
            tmpValue = null;
            visitUnaryExp(mulExpBlock.unaryExpBlock);
            if(value!=null){
                tmpValue = buildFactory.buildBinary(curBlock, op, value, tmpValue);
            }
            if(mulExpBlock.mulExpBlock!=null){
                if(mulExpBlock.op.type==TokenType.MULT){
                    tmpOp = Operator.Mul;
                } else if(mulExpBlock.op.type==TokenType.DIV){
                    tmpOp = Operator.Div;
                } else if(mulExpBlock.op.type==TokenType.MOD){
                    tmpOp = Operator.Mod;
                } else {
                    System.out.println("Error In IrBuilder: MulExp Error");
                }
                visitMulExp(mulExpBlock.mulExpBlock);
            }
        }
    }
    private void visitAddExp(AddExpBlock addExpBlock) {
        if(isConst){
            //常量直接计算
            Integer value = saveValue;
            Operator op = saveOp;
            saveValue = null;
            visitMulExp(addExpBlock.mulExpBlock);
            if (value != null) {
                saveValue = calculate(op, value, saveValue);
            }
            if (addExpBlock.addExpBlock != null) {
                saveOp = addExpBlock.op.type == TokenType.PLUS ? Operator.Add : Operator.Sub;
                visitAddExp(addExpBlock.addExpBlock);
            }
        }else {
            //不是常量比较麻烦
            //代替传参
            Operator op = tmpOp;
            Value value = tmpValue;
            /*
              TODO: 这里可以把连加优化成乘法
              但是我懒得优化:)
             */

            //递归下降
            tmpValue = null;
            visitMulExp(addExpBlock.mulExpBlock);
            if(value!=null){
                tmpValue = buildFactory.buildBinary(curBlock,op,value,tmpValue);
            }
            if(addExpBlock.addExpBlock!=null){
                if(addExpBlock.op.type == TokenType.PLUS){
                    tmpOp = Operator.Add;
                }else {
                    tmpOp = Operator.Sub;
                }
                visitAddExp(addExpBlock.addExpBlock);
            }
        }
    }
    private void visitRelExp(RelExpBlock relExpBlock) {
        //代替传参
        Operator op = tmpOp;
        Value value = tmpValue;

        //递归下降
        tmpValue = null;
        visitAddExp(relExpBlock.addExpBlock);

        if(value!=null){
            //如果是自身递归调用才会运行
            tmpValue = buildFactory.buildBinary(curBlock, op, value, tmpValue);
        }
        if (relExpBlock.relExpBlock != null) {
            TokenType tokenType = relExpBlock.op.type;
            if(tokenType==TokenType.LSS){
                tmpOp = Operator.Lt;
            } else if(tokenType==TokenType.LEQ){
                tmpOp = Operator.Le;
            }else if(tokenType==TokenType.GRE){
                tmpOp = Operator.Gt;
            }else if(tokenType==TokenType.GEQ){
                tmpOp = Operator.Ge;
            }else{
                System.out.println("""
                        ====================================
                        Error In IrBuilder: RelExp Op Error
                        ====================================
                        """);
            }
            visitRelExp(relExpBlock.relExpBlock);
        }
    }
    private void visitEqExp(EqExpBlock eqExpBlock) {
        //代替传参
        Operator op = tmpOp;
        Value value = tmpValue;

        //递归下降
        tmpValue = null;
        visitRelExp(eqExpBlock.relExpBlock);

        if (value != null) {
            //如果是自身递归调用才会运行
            tmpValue = buildFactory.buildBinary(curBlock, op, value, tmpValue);
        }

        if (eqExpBlock.eqExpBlock != null) {
            //向下一个自身传参
            if(eqExpBlock.op.type==TokenType.EQL){
                tmpOp = Operator.Eq;
            } else {
                tmpOp = Operator.Ne;
            }
            visitEqExp(eqExpBlock.eqExpBlock);
        }
    }

    private void visitLAndExp(LAndExpBlock lAndExpBlock) {
        BasicBlock trueBlock = curTrueBlock;
        BasicBlock falseBlock = curFalseBlock;
        BasicBlock thenBlock = null;

        if (lAndExpBlock.lAndExpBlock != null) {
            thenBlock = buildFactory.buildBasicBlock(curFunction);
            curTrueBlock = thenBlock;
        }

        //递归下降
        tmpValue = null;
        visitEqExp(lAndExpBlock.eqExpBlock);
        buildFactory.buildBr(curBlock,tmpValue,curTrueBlock,curFalseBlock);
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;

        if(lAndExpBlock.lAndExpBlock!=null){
            curBlock = thenBlock;
            visitLAndExp(lAndExpBlock.lAndExpBlock);
        }
    }
    private void visitLOrExp(LOrExpBlock lOrExpBlock) {
        BasicBlock trueBlock = curTrueBlock;
        BasicBlock falseBlock = curFalseBlock;
        BasicBlock thenBlock = null;
        if (lOrExpBlock.lOrExpBlock != null) {
            thenBlock = buildFactory.buildBasicBlock(curFunction);
            curFalseBlock = thenBlock;
        }

        //递归下降
        tmpValue = null;
        visitLAndExp(lOrExpBlock.lAndExpBlock);

        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
        if (lOrExpBlock.lOrExpBlock != null) {
            curBlock = thenBlock;
            visitLOrExp(lOrExpBlock.lOrExpBlock);
        }
    }
    private void visitConstExp(ConstExpBlock constExpBlock) {
        isConst = true;
        saveValue = null;
        visitAddExp(constExpBlock.addExpBlock);
        isConst = false;
    }

}
