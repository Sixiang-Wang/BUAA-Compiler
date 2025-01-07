package Frontend;

import Block.*;
import Token.Token;
import Token.*;
import Error.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private static final Parser instance = new Parser();
    public static Parser getInstance() { return instance; }
    private List<Token> tokenList;
    private Integer maxToken;
    public void initialize(List<Token> tokenList) {
        this.tokenList = tokenList;
        maxToken = tokenList.size()-1;
    }
    private CompUnitBlock compUnitBlock;
    public CompUnitBlock getCompUnitBlock() {
        return compUnitBlock;
    }
    public static Map<BlockType, String> blockType = new HashMap<>() {{
        put(BlockType.CompUnit, "<CompUnit>");
        put(BlockType.Decl, "<Decl>");
        put(BlockType.ConstDecl, "<ConstDecl>");
        put(BlockType.BType, "<BType>");
        put(BlockType.ConstDef, "<ConstDef>");
        put(BlockType.ConstInitVal, "<ConstInitVal>");
        put(BlockType.VarDecl, "<VarDecl>");
        put(BlockType.VarDef, "<VarDef>");
        put(BlockType.InitVal, "<InitVal>");
        put(BlockType.FuncDef, "<FuncDef>");
        put(BlockType.MainFuncDef, "<MainFuncDef>");
        put(BlockType.FuncType, "<FuncType>");
        put(BlockType.FuncFParams, "<FuncFParams>");
        put(BlockType.FuncFParam, "<FuncFParam>");
        put(BlockType.Block, "<Block>");
        put(BlockType.BlockItem, "<BlockItem>");
        put(BlockType.Stmt, "<Stmt>");
        put(BlockType.ForStmt, "<ForStmt>");
        put(BlockType.Exp, "<Exp>");
        put(BlockType.Cond, "<Cond>");
        put(BlockType.LVal, "<LVal>");
        put(BlockType.PrimaryExp, "<PrimaryExp>");
        put(BlockType.Number, "<Number>");
        put(BlockType.Character, "<Character>");
        put(BlockType.UnaryExp, "<UnaryExp>");
        put(BlockType.UnaryOp, "<UnaryOp>");
        put(BlockType.FuncRParams, "<FuncRParams>");
        put(BlockType.MulExp, "<MulExp>");
        put(BlockType.AddExp, "<AddExp>");
        put(BlockType.RelExp, "<RelExp>");
        put(BlockType.EqExp, "<EqExp>");
        put(BlockType.LAndExp, "<LAndExp>");
        put(BlockType.LOrExp, "<LOrExp>");
        put(BlockType.ConstExp, "<ConstExp>");
    }};

    private Token nowToken;
    private int now;

    public void analyze(){
        now = 0;
        nowToken = tokenList.get(now);
        this.compUnitBlock = CompUnit();
    }

    public CompUnitBlock CompUnit(){
        List<DeclBlock> declBlockList = new ArrayList<>();
        List<FuncDefBlock> funcDefBlockList = new ArrayList<>();
        MainFuncDefBlock mainFuncDefBlock = null;

        while (tokenList.get(now + 1).type != TokenType.MAINTK && tokenList.get(now + 2).type != TokenType.LPARENT) {
            nowToken = tokenList.get(now);
            DeclBlock declBlock = Decl();
            declBlockList.add(declBlock);
        }
        while (tokenList.get(now + 1).type != TokenType.MAINTK) {
            FuncDefBlock funcDefBlock = FuncDef();
            funcDefBlockList.add(funcDefBlock);
        }
        mainFuncDefBlock = MainFuncDef();
        return new CompUnitBlock(declBlockList,funcDefBlockList,mainFuncDefBlock);
    }

    public DeclBlock Decl(){
        ConstDeclBlock constDeclBlock = null;
        VarDeclBlock varDeclBlock = null;
        if(nowToken.type == TokenType.CONSTTK){
            constDeclBlock = ConstDecl();
        } else {
            varDeclBlock = VarDecl();
        }

        return new DeclBlock(constDeclBlock,varDeclBlock);
    }

    public ConstDeclBlock ConstDecl() {
        Token constToken = getToken(TokenType.CONSTTK);
        BTypeBlock bTypeBlock = BType();
        List<ConstDefBlock> constDefBlockList = new ArrayList<>();
        List<Token> commaList = new ArrayList<>();
        constDefBlockList.add(ConstDef());
        while(nowToken.type == TokenType.COMMA){
            commaList.add(getToken(TokenType.COMMA));
            constDefBlockList.add(ConstDef());
        }
        Token semicnToken = getToken(TokenType.SEMICN);
        return new ConstDeclBlock(constToken,bTypeBlock,constDefBlockList,commaList,semicnToken);
    }

    public BTypeBlock BType() {
        Token token = null;
        if(nowToken.type == TokenType.INTTK){
            token = getToken(TokenType.INTTK);
        } else {
            token = getToken(TokenType.CHARTK);
        }
        return new BTypeBlock(token);
    }

    public ConstDefBlock ConstDef() {
        Token ident = getToken(TokenType.IDENFR);
        Token leftBracket = null;
        ConstExpBlock constExpBlock = null;
        Token rightBracket = null;

        if(nowToken.type == TokenType.LBRACK){
            leftBracket=getToken(TokenType.LBRACK);
            constExpBlock=ConstExp();
            rightBracket=getToken(TokenType.RBRACK);
        }
        Token assign = getToken(TokenType.ASSIGN);
        ConstInitValBlock constInitValBlock = ConstInitVal();

        return new ConstDefBlock(ident,leftBracket,constExpBlock,rightBracket,assign,constInitValBlock);
    }

    public ConstInitValBlock ConstInitVal() {
        List<ConstExpBlock> constExpBlockList = new ArrayList<>();
        Token leftBraceToken = null;
        List<Token> commaList = new ArrayList<>();
        Token rightBraceToken = null;
        if(nowToken.type == TokenType.LBRACE){
            leftBraceToken = getToken(TokenType.LBRACE);
            if(nowToken.type != TokenType.RBRACE){
                constExpBlockList.add(ConstExp());
                while(nowToken.type != TokenType.RBRACE){
                    commaList.add(getToken(TokenType.COMMA));
                    constExpBlockList.add(ConstExp());
                }
            }
            rightBraceToken = getToken(TokenType.RBRACE);
            return new ConstInitValBlock(leftBraceToken,constExpBlockList,commaList,rightBraceToken);
        } else if (nowToken.type == TokenType.STRCON) {
            Token stringConst = getToken(TokenType.STRCON);
            return new ConstInitValBlock(stringConst);
        } else {
            constExpBlockList.add(ConstExp());
            return new ConstInitValBlock(constExpBlockList);
        }
    }

    public VarDeclBlock VarDecl() {
        BTypeBlock bTypeBlock;
        List<VarDefBlock> varDefBlockList = new ArrayList<>();
        List<Token> commaList = new ArrayList<>();
        Token semicn;

        bTypeBlock = BType();
        varDefBlockList.add(VarDef());
        while(nowToken.type==TokenType.COMMA){
            commaList.add(getToken(TokenType.COMMA));
            varDefBlockList.add(VarDef());
        }
        semicn = getToken(TokenType.SEMICN);
        return new VarDeclBlock(bTypeBlock,varDefBlockList,commaList,semicn);
    }

    public VarDefBlock VarDef() {
        Token ident = null;
        Token lb = null;
        ConstExpBlock constExpBlock = null;
        Token rb = null;
        Token assign = null;
        InitValBlock initValBlock = null;

        ident = getToken(TokenType.IDENFR);
        if(nowToken.type == TokenType.LBRACK){
            lb = getToken(TokenType.LBRACK);
            constExpBlock = ConstExp();
            rb = getToken(TokenType.RBRACK);
        }
        if(nowToken.type == TokenType.ASSIGN){
            assign = getToken(TokenType.ASSIGN);
            initValBlock = InitVal();
        }
        return new VarDefBlock(ident,lb,constExpBlock,rb,assign,initValBlock);
    }

    public InitValBlock InitVal() {
        List<ExpBlock> expBlockList = new ArrayList<>();
        Token leftBraceToken = null;
        List<Token> commas = new ArrayList<>();
        Token rightBraceToken = null;
        Token stringConst = null;

        if(nowToken.type == TokenType.LBRACE){
            leftBraceToken = getToken(TokenType.LBRACE);
            if(nowToken.type != TokenType.RBRACE){
                expBlockList.add(Exp());
                while (nowToken.type != TokenType.RBRACE) {
                    commas.add(getToken(TokenType.COMMA));
                    expBlockList.add(Exp());
                }
            }
            rightBraceToken = getToken(TokenType.RBRACE);
            return new InitValBlock(leftBraceToken,expBlockList,commas,rightBraceToken);
        } else if (nowToken.type == TokenType.STRCON) {
            stringConst = getToken(TokenType.STRCON);
            return new InitValBlock(stringConst);
        } else {
            expBlockList.add(Exp());
            return new InitValBlock(expBlockList);
        }
    }

    public FuncDefBlock FuncDef() {
        FuncTypeBlock funcTypeBlock = FuncType();
        Token ident = getToken(TokenType.IDENFR);
        Token leftParent = getToken(TokenType.LPARENT);
        FuncFParamsBlock funcFParamsBlock = null;
        if(isBType()){
            funcFParamsBlock = FuncFParams();
        }
        Token rightParent = getToken(TokenType.RPARENT);
        BlockBlock blockBlock = Block();

        return new FuncDefBlock(funcTypeBlock,ident,leftParent,funcFParamsBlock,rightParent,blockBlock);
    }

    public MainFuncDefBlock MainFuncDef() {
        Token intToken = getToken(TokenType.INTTK);
        Token mainToken = getToken(TokenType.MAINTK);
        Token leftParentToken = getToken(TokenType.LPARENT);
        Token rightParentToken = getToken(TokenType.RPARENT);
        BlockBlock blockBlock = Block();
        return new MainFuncDefBlock(intToken,mainToken,leftParentToken,rightParentToken,blockBlock);
    }

    public FuncTypeBlock FuncType() {
        Token token = null;
        if(nowToken.type == TokenType.VOIDTK){
            token = getToken(TokenType.VOIDTK);
        } else if(nowToken.type == TokenType.INTTK){
            token = getToken(TokenType.INTTK);
        } else{
            token = getToken(TokenType.CHARTK);
        }
        return new FuncTypeBlock(token);
    }

    public FuncFParamsBlock FuncFParams() {
        List<FuncFParamBlock> funcFParamBlockList = new ArrayList<>();
        List<Token> commaList = new ArrayList<>();
        funcFParamBlockList.add(FuncFParam());
        while(nowToken.type == TokenType.COMMA){
            commaList.add(getToken(TokenType.COMMA));
            funcFParamBlockList.add((FuncFParam()));
        }
        return new FuncFParamsBlock(funcFParamBlockList,commaList);
    }

    public FuncFParamBlock FuncFParam() {
        BTypeBlock bTypeBlock = BType();
        Token ident = getToken(TokenType.IDENFR);
        Token leftBrackets = null;
        Token rightBrackets = null;
        if(nowToken.type == TokenType.LBRACK){
            leftBrackets = getToken(TokenType.LBRACK);
            rightBrackets = getToken(TokenType.RBRACK);
        }
        return new FuncFParamBlock(bTypeBlock,ident,leftBrackets,rightBrackets);
    }

    public BlockBlock Block() {
        Token leftBraceToken = getToken(TokenType.LBRACE);
        List<BlockItemBlock> blockItemBlockList = new ArrayList<>();
        while(nowToken.type!=TokenType.RBRACE){
            blockItemBlockList.add(BlockItem());
        }
        Token rightBraceToken = getToken(TokenType.RBRACE);
        return new BlockBlock(leftBraceToken,blockItemBlockList,rightBraceToken);
    }

    public BlockItemBlock BlockItem() {
        DeclBlock declBlock = null;
        StmtBlock stmtBlock = null;

        if(nowToken.type == TokenType.CONSTTK || isBType()){
            declBlock = Decl();
        } else {

            stmtBlock = Stmt();
        }
        return new BlockItemBlock(declBlock,stmtBlock);
    }

    public StmtBlock Stmt() {
        //Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
        //| [Exp] ';' //有无Exp两种情况
        //| Block
        //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省，1种情况 2.
        //ForStmt与Cond中缺省一个，3种情况 3. ForStmt与Cond中缺省两个，3种情况 4. ForStmt与Cond全部
        //缺省，1种情况
        //| 'break' ';' | 'continue' ';'
        //| 'return' [Exp] ';' // 1.有Exp 2.无Exp
        //| LVal '=' 'getint''('')'';'
        //| LVal '=' 'getchar''('')'';'
        //| 'printf''('StringConst {','Exp}')'';' // 1.有Exp 2.无Exp

        if (nowToken.type == TokenType.LBRACE) {
            //Block
            return new StmtBlock(StmtType.Block,Block());
        } else if (nowToken.type == TokenType.IFTK) {
            //'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            Token ifToken = getToken(TokenType.IFTK);
            Token lpToken = getToken(TokenType.LPARENT);
            CondBlock condBlock = Cond();
            Token rpToken = getToken(TokenType.RPARENT);
            List<StmtBlock> stmtBlockList = new ArrayList<>();
            stmtBlockList.add(Stmt());
            Token elseToken = null;
            if(nowToken.type == TokenType.ELSETK){
                elseToken = getToken(TokenType.ELSETK);
                stmtBlockList.add(Stmt());
            }
            return new StmtBlock(StmtType.If,ifToken,lpToken,condBlock,rpToken,stmtBlockList,elseToken);
        } else if (nowToken.type == TokenType.FORTK) {
            //'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            Token forToken = getToken(TokenType.FORTK);
            Token lpToken = getToken(TokenType.LPARENT);
            ForStmtBlock forStmtBlock1 = null;
            ForStmtBlock forStmtBlock2 = null;
            Token forSemicn1 = null;
            Token forSemicn2 = null;
            if(nowToken.type!=TokenType.SEMICN){
                forStmtBlock1 = ForStmt();
            }
            forSemicn1 = getToken(TokenType.SEMICN);
            CondBlock condBlock = null;
            if(nowToken.type!=TokenType.SEMICN){
                condBlock = Cond();
            }
            forSemicn2 = getToken(TokenType.SEMICN);
            if(nowToken.type!=TokenType.RPARENT){
                forStmtBlock2 = ForStmt();
            }
            Token rp = getToken(TokenType.RPARENT);
            StmtBlock stmtBlock = Stmt();

            return new StmtBlock(StmtType.For,forToken,lpToken,forStmtBlock1,forSemicn1,condBlock,forSemicn2,forStmtBlock2,rp,stmtBlock);

        } else if (nowToken.type == TokenType.BREAKTK || nowToken.type == TokenType.CONTINUETK) {
            //'break' ';' | 'continue' ';'
            Token token = null;
            StmtType type = null;
            if(nowToken.type == TokenType.BREAKTK){
                token = getToken(TokenType.BREAKTK);
                type = StmtType.Break;
            } else{
                token = getToken(TokenType.CONTINUETK);
                type = StmtType.Continue;
            }
            Token semicn = getToken(TokenType.SEMICN);
            return new StmtBlock(type,token,semicn);
        } else if (nowToken.type == TokenType.RETURNTK) {
            //'return' [Exp] ';'
            Token returnToken = getToken(TokenType.RETURNTK);
            ExpBlock expBlock = null;
            if(nowToken.type!=TokenType.SEMICN){
                expBlock = Exp();
            }
            Token semicn = getToken(TokenType.SEMICN);
            return new StmtBlock(StmtType.Return,returnToken,expBlock,semicn);
        } else if (nowToken.type == TokenType.PRINTFTK) {
            //'printf''('StringConst {','Exp}')'';'
            Token printf = getToken(TokenType.PRINTFTK);
            Token lpToken = getToken(TokenType.LPARENT);
            Token strCon = getToken(TokenType.STRCON);
            List<Token> commaList = new ArrayList<>();
            List<ExpBlock> expBlockList = new ArrayList<>();
            while(nowToken.type == TokenType.COMMA){
                commaList.add(getToken(TokenType.COMMA));
                expBlockList.add(Exp());
            }
            Token rpToken = getToken(TokenType.RPARENT);
            Token semicn = getToken(TokenType.SEMICN);
            return new StmtBlock(StmtType.Printf,printf,lpToken,strCon,commaList,expBlockList,rpToken,semicn);
        } else {
            boolean flag = false;
            for(int i = now;i<=maxToken && tokenList.get(i).line.equals(nowToken.line);i++){
                if (tokenList.get(i).type == TokenType.ASSIGN) {
                    flag = true;
                    break;
                }
            }
            if(flag){
                LValBlock lValBlock = LVal();
                Token assign = getToken(TokenType.ASSIGN);
                if(nowToken.type == TokenType.GETINTTK){
                    //LVal '=' 'getint''('')'';'
                    Token getint = getToken(TokenType.GETINTTK);
                    Token lpToken = getToken(TokenType.LPARENT);
                    Token rpToken = getToken(TokenType.RPARENT);
                    Token semicn = getToken(TokenType.SEMICN);
                    return new StmtBlock(StmtType.GetInt,lValBlock,assign,getint,lpToken,rpToken,semicn);
                } else if(nowToken.type == TokenType.GETCHARTK){
                    //LVal '=' 'getchar''('')'';'
                    Token getint = getToken(TokenType.GETCHARTK);
                    Token lpToken = getToken(TokenType.LPARENT);
                    Token rpToken = getToken(TokenType.RPARENT);
                    Token semicn = getToken(TokenType.SEMICN);
                    return new StmtBlock(StmtType.GetChar,lValBlock,assign,getint,lpToken,rpToken,semicn);
                } else {
                    //LVal '=' Exp ';'
                    ExpBlock expBlock = Exp();
                    Token semicn = getToken(TokenType.SEMICN);
                    return new StmtBlock(StmtType.LValAssignExp,lValBlock,assign,expBlock,semicn);
                }
            }else {
                if (isExp()){
                    //Exp ';'
                    ExpBlock expBlock = Exp();
                    Token semicnToken = getToken(TokenType.SEMICN);
                    return new StmtBlock(StmtType.Exp,expBlock,semicnToken);
                } else if (nowToken.type == TokenType.SEMICN) {
                    //';'
                    Token semicnToken = getToken(TokenType.SEMICN);
                    return new StmtBlock(StmtType.Exp,(ExpBlock) null,semicnToken);
                }
            }
        }
        return null;
    }

    public ForStmtBlock ForStmt() {
        LValBlock lValBlock = LVal();
        Token op = getToken(TokenType.ASSIGN);
        ExpBlock expBlock = Exp();
        return new ForStmtBlock(lValBlock,op,expBlock);
    }

    public ExpBlock Exp() {
        return new ExpBlock(AddExp());
    }

    public CondBlock Cond() {
        return new CondBlock(LOrExp());
    }

    public LValBlock LVal() {

        Token ident = getToken(TokenType.IDENFR);
        Token leftBracket = null;
        ExpBlock expBlock = null;
        Token rightBracket = null;
        if(nowToken.type == TokenType.LBRACK){
            leftBracket = getToken(TokenType.LBRACK);
            expBlock = Exp();
            rightBracket = getToken(TokenType.RBRACK);
        }
        return new LValBlock(ident,leftBracket,expBlock,rightBracket);
    }

    public PrimaryExpBlock PrimaryExp() {
        Token leftParentToken = null;
        ExpBlock expBlock = null;
        Token rightParentToken = null;
        LValBlock lValBlock = null;
        NumberBlock numberBlock = null;
        CharacterBlock characterBlock = null;

        if(nowToken.type == TokenType.LPARENT){
            leftParentToken = getToken(TokenType.LPARENT);
            expBlock = Exp();
            rightParentToken = getToken(TokenType.RPARENT);
            return new PrimaryExpBlock(leftParentToken,expBlock,rightParentToken);
        } else if (nowToken.type == TokenType.INTCON) {
            numberBlock = Number();
            return new PrimaryExpBlock(numberBlock);
        } else if (nowToken.type == TokenType.CHRCON) {
            characterBlock = Character();
            return new PrimaryExpBlock(characterBlock);
        } else {
            lValBlock = LVal();
            return new PrimaryExpBlock(lValBlock);
        }
    }

    public NumberBlock Number() {
        return new NumberBlock(getToken(TokenType.INTCON));
    }

    public CharacterBlock Character() {
        return new CharacterBlock(getToken(TokenType.CHRCON));
    }

    public UnaryExpBlock UnaryExp() {
        PrimaryExpBlock primaryExpBlock = null;
        Token ident = null;
        Token leftParentToken = null;
        FuncRParamsBlock funcRParamsBlock = null;
        Token rightParentToken = null;
        UnaryOpBlock unaryOpBlock = null;
        UnaryExpBlock unaryExpBlock = null;

        if(isUnaryOp()){
            unaryOpBlock = UnaryOp();
            unaryExpBlock = UnaryExp();
            return new UnaryExpBlock(unaryOpBlock,unaryExpBlock);
        } else if (nowToken.type == TokenType.IDENFR && tokenList.get(now+1).type == TokenType.LPARENT) {
            ident = getToken(TokenType.IDENFR);
            leftParentToken = getToken(TokenType.LPARENT);
            if(isExp()){
                funcRParamsBlock = FuncRParams();
            }
            rightParentToken = getToken(TokenType.RPARENT);
            return new UnaryExpBlock(ident,leftParentToken,funcRParamsBlock,rightParentToken);
        } else {
            primaryExpBlock = PrimaryExp();
            return new UnaryExpBlock(primaryExpBlock);
        }
    }


    public UnaryOpBlock UnaryOp() {
        Token token;
        if(nowToken.type == TokenType.PLUS){
            token = getToken(TokenType.PLUS);
        } else  if(nowToken.type == TokenType.MINU){
            token = getToken(TokenType.MINU);
        } else{
            token = getToken(TokenType.NOT);
        }
        return new UnaryOpBlock(token);
    }

    public FuncRParamsBlock FuncRParams() {
        List<ExpBlock> expBlockList = new ArrayList<>();
        List<Token> commaList = new ArrayList<>();
        expBlockList.add(Exp());
        while(nowToken.type == TokenType.COMMA){
            commaList.add(getToken(TokenType.COMMA));
            expBlockList.add(Exp());
        }
        return new FuncRParamsBlock(expBlockList,commaList);
    }

    public MulExpBlock MulExp() {
        UnaryExpBlock unaryExpBlock = UnaryExp();
        Token op = null;
        MulExpBlock mulExpBlock = null;

        if(nowToken.type == TokenType.MULT){
            op = getToken(TokenType.MULT);
            mulExpBlock = MulExp();
        } else if (nowToken.type == TokenType.DIV) {
            op = getToken(TokenType.DIV);
            mulExpBlock = MulExp();
        } else if (nowToken.type == TokenType.MOD)  {
            op = getToken(TokenType.MOD);
            mulExpBlock = MulExp();
        }

        return new MulExpBlock(unaryExpBlock,op,mulExpBlock);
    }

    public AddExpBlock AddExp() {
        MulExpBlock mulExpBlock = MulExp();
        Token op = null;
        AddExpBlock addExpBlock = null;

        if(nowToken.type == TokenType.PLUS){
            op = getToken(TokenType.PLUS);
            addExpBlock = AddExp();
        } else if (nowToken.type == TokenType.MINU) {
            op = getToken(TokenType.MINU);
            addExpBlock = AddExp();
        }
        return new AddExpBlock(mulExpBlock,op,addExpBlock);
    }

    public RelExpBlock RelExp() {
        AddExpBlock addExpBlock = AddExp();
        Token op = null;
        RelExpBlock relExpBlock = null;

        if(nowToken.type == TokenType.LSS){
            op = getToken(TokenType.LSS);
            relExpBlock = RelExp();
        } else if (nowToken.type == TokenType.GRE) {
            op = getToken(TokenType.GRE);
            relExpBlock = RelExp();
        } else if (nowToken.type == TokenType.LEQ) {
            op = getToken(TokenType.LEQ);
            relExpBlock = RelExp();
        } else if (nowToken.type == TokenType.GEQ) {
            op = getToken(TokenType.GEQ);
            relExpBlock = RelExp();
        }
        return new RelExpBlock(addExpBlock,op,relExpBlock);
    }

    public EqExpBlock EqExp() {
        RelExpBlock relExpBlock = RelExp();
        EqExpBlock eqExpBlock = null;
        Token op = null;

        if(nowToken.type == TokenType.EQL){
            op = getToken(TokenType.EQL);
            eqExpBlock = EqExp();
        } else if (nowToken.type == TokenType.NEQ) {
            op = getToken(TokenType.NEQ);
            eqExpBlock = EqExp();
        }
        return new EqExpBlock(relExpBlock,op,eqExpBlock);
    }

    public LAndExpBlock LAndExp() {
        EqExpBlock eqExpBlock = EqExp();
        LAndExpBlock lAndExpBlock = null;
        Token op = null;

        if(nowToken.type == TokenType.AND){
            op = getToken(TokenType.AND);
            lAndExpBlock = LAndExp();
        }
        return new LAndExpBlock(lAndExpBlock,op,eqExpBlock);
    }

    public LOrExpBlock LOrExp() {
        LAndExpBlock lAndExpBlock = LAndExp();
        Token op = null;
        LOrExpBlock lOrExpBlock = null;

        if(nowToken.type == TokenType.OR){
            op = getToken(TokenType.OR);
            lOrExpBlock = LOrExp();
        }
        return new LOrExpBlock(lAndExpBlock,op,lOrExpBlock);
    }

    public ConstExpBlock ConstExp() {
        AddExpBlock addExpBlock = AddExp();

        return new ConstExpBlock(addExpBlock);
    }


    public boolean isBType(){
        return nowToken.type == TokenType.INTTK ||
                nowToken.type == TokenType.CHARTK;
    }
    public boolean isUnaryOp(){
        return nowToken.type == TokenType.PLUS ||
                nowToken.type == TokenType.MINU ||
                nowToken.type == TokenType.NOT;
    }
    public boolean isExp(){
        return nowToken.type == TokenType.LPARENT ||
                nowToken.type == TokenType.INTCON ||
                nowToken.type == TokenType.CHRCON ||
                nowToken.type == TokenType.IDENFR ||
                nowToken.type == TokenType.PLUS ||
                nowToken.type == TokenType.MINU ||
                nowToken.type == TokenType.NOT;
    }

    public Token getToken(TokenType tokenType){
        Token tmp = nowToken;
        if(tmp.type == tokenType){
            if(now < maxToken){
                now++;
                nowToken = tokenList.get(now);
            }
            return tmp;
        } else {
            Token before = tokenList.get(now-1);

            if(tokenType == TokenType.SEMICN){
                ErrorHandler.add(before.line,ErrType.i);
                return new Token(TokenType.SEMICN,";", before.line);
            } else if (tokenType == TokenType.RPARENT) {
                ErrorHandler.add(before.line, ErrType.j);
                return new Token(TokenType.RPARENT,")", before.line);
            } else if (tokenType == TokenType.RBRACK) {
                ErrorHandler.add(before.line, ErrType.k);
                return new Token(TokenType.RBRACK,"]", before.line);
            }else{
                throw new RuntimeException("token const not right: " + tmp.token + " at line "+tmp.line);
            }
        }
    }

    public static String getBlockType(BlockType type){
        return blockType.get(type);
    }
}
