package Frontend;
import Token.*;
import Error.*;

import java.io.*;
import java.util.*;

public class Lexer {

    private final String input;
    int len;
    int lineNum = 0;
    int now = 0;
    char c;


    List<Token> tokenList = new ArrayList<>();


    HashMap<String, TokenType> tokenMap = new HashMap<>() {{
        put("main", TokenType.MAINTK);
        put("const", TokenType.CONSTTK);
        put("int", TokenType.INTTK);
        put("char", TokenType.CHARTK);
        put("break", TokenType.BREAKTK);
        put("continue", TokenType.CONTINUETK);
        put("if", TokenType.IFTK);
        put("else", TokenType.ELSETK);
        put("for", TokenType.FORTK);
        put("getint", TokenType.GETINTTK);
        put("getchar", TokenType.GETCHARTK);
        put("printf", TokenType.PRINTFTK);
        put("return", TokenType.RETURNTK);
        put("void", TokenType.VOIDTK);

        put("+", TokenType.PLUS);
        put("-", TokenType.MINU);
        put("*", TokenType.MULT);
        put("%", TokenType.MOD);
        put(";", TokenType.SEMICN);
        put(",", TokenType.COMMA);
        put("(", TokenType.LPARENT);
        put(")", TokenType.RPARENT);
        put("[", TokenType.LBRACK);
        put("]", TokenType.RBRACK);
        put("{", TokenType.LBRACE);
        put("}", TokenType.RBRACE);

        put("!", TokenType.NOT);
        put("!=", TokenType.NEQ);
        put("&&", TokenType.AND);
        put("||", TokenType.OR);
        put("<", TokenType.LSS);
        put("<=", TokenType.LEQ);
        put(">", TokenType.GRE);
        put(">=", TokenType.GEQ);
        put("=", TokenType.ASSIGN);
        put("==", TokenType.EQL);
    }};

    public List<Token> getTokenList(){
        return tokenList;
    }

    public Lexer(String input){
        this.input = input;
    }
    public void analyze(){
        try {
            len = input.length();
            lineNum = 1;
            now = 0;


            while(now<len){

                c = input.charAt(now);

                if ((c >='a'&& c <='z')||(c >='A'&& c <='Z')|| c =='_'){
                    analyseWord();
                } else if (c >='0' && c <='9') {
                    analyseNumber();
                } else if ( c == ' ' || c == '\t' ){
                    now++;
                } else if ( c == '\n' || c == '\r'){
                    lineNum++;
                    now++;
                } else if(c=='\"'){
                    analyseString();
                } else if(c=='\''){
                    analyseChar();
                } else if(c == '/'){
                    now++;
                    c = input.charAt(now);

                    if(c=='/'){
                        while(now<len){
                            if(input.charAt(now)=='\n'){
                                break;
                            }
                            now++;
                        }
                    } else if (c=='*') {
                        boolean flag = false;
                        while(true){
                            if(c=='*'){
                                flag = true;
                            } else if(c=='/'&& flag){
                                now++;
                                break;
                            } else {
                                flag = false;
                                if(c=='\n'){
                                    lineNum++;
                                }
                            }
                            now++;
                            c = input.charAt(now);
                        }
                    } else{
                        Token token = new Token(TokenType.DIV,"/",lineNum);
                        tokenList.add(token);
                    }
                }
                else if(c=='+'||c=='-'||c=='*'||c=='%'||c==';'||c==','||c=='('||c==')'||c=='['||c==']'||c=='{'||c=='}'){
                    analyseSingle();
                }
                else if(c=='!'||c=='&'||c=='|'||c=='<'||c=='>'||c=='='){
                    analyseMulti();
                } else {
                    now++;
                }
            }

            writeFile(tokenList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void analyseWord(){
        StringBuilder tmp = new StringBuilder();
        while(now<len){
            c = input.charAt(now);
            if ((c >='a'&& c <='z')||(c >='A'&& c <='Z')|| c =='_'||(c>='0'&&c<='9')){
                tmp.append(c);
                now++;
            }
            else{
                break;
            }
        }
        String result = tmp.toString();
        TokenType type = tokenMap.getOrDefault(result, TokenType.IDENFR);
        Token token = new Token(type,result,lineNum);
        tokenList.add(token);
    }
    public void analyseNumber(){
        StringBuilder tmp = new StringBuilder();
        while(now<len){
            c = input.charAt(now);
            if (c >='0'&& c <='9'){
                tmp.append(c);
                now++;
            }
            else{
                break;
            }
        }
        String result = tmp.toString();
        TokenType type = TokenType.INTCON;
        Token token = new Token(type,result,lineNum);
        tokenList.add(token);
    }
    public void analyseString(){
        StringBuilder tmp = new StringBuilder();
        tmp.append('"');
        now++;
        while(now<len){
            c = input.charAt(now);
            if (c != '"'){
                tmp.append(c);
                if(c=='\\'){
                    if(input.charAt(now+1)=='"'){
                        now++;
                        c = input.charAt(now);
                        tmp.append(c);
                    }
                } else if (c=='\n') {
                    lineNum++;
                }

                now++;
            }
            else{
                tmp.append('"');
                String result = tmp.toString();
                TokenType type = TokenType.STRCON;
                Token token = new Token(type,result,lineNum);
                tokenList.add(token);
                break;
            }
        }
        now++;
    }

    public void analyseChar(){
        StringBuilder tmp = new StringBuilder();
        tmp.append('\'');
        now++;
        while(now<len){
            c = input.charAt(now);
            if (c != '\''){
                tmp.append(c);
                if(c=='\\'){
                    if(input.charAt(now+1)=='\''){
                        now++;
                        c = input.charAt(now);
                        tmp.append(c);
                    }else if(input.charAt(now+1)=='\\'){
                        now++;
                        c = input.charAt(now);
                        tmp.append(c);
                    }
                } else if (c=='\n') {
                    now++;
                }
                now++;
            }
            else{
                tmp.append('\'');

                String result = tmp.toString();
                TokenType type = TokenType.CHRCON;
                Token token = new Token(type,result,lineNum);
                tokenList.add(token);

                break;
            }
        }
        now++;
    }

    public void analyseSingle(){
        String result = String.valueOf(c);
        TokenType type = tokenMap.get(result);
        Token token = new Token(type,result,lineNum);
        tokenList.add(token);
        now++;
    }

    public void analyseMulti(){
        StringBuilder tmp = new StringBuilder();
        String result;
        c = input.charAt(now);
        tmp.append(c);
        boolean hasNext = now < len - 1;
        TokenType type;
        if(c=='!'&&hasNext&& input.charAt(now+1)=='='){
            now++;
            c = input.charAt(now);
            tmp.append(c);
        } else if (c=='&') {
            if(hasNext&& input.charAt(now+1)=='&'){
                now++;
                c = input.charAt(now);
                tmp.append(c);
            } else {
                ErrorHandler.add(lineNum,ErrType.a);
                tmp.append(c);
            }

        } else if (c=='|') {
            if(hasNext&& input.charAt(now+1)=='|'){
                now++;
                c = input.charAt(now);
                tmp.append(c);
            } else {
                ErrorHandler.add(lineNum,ErrType.a);
                tmp.append(c);
            }
        } else if (c=='<'&&hasNext&& input.charAt(now+1)=='=') {
            now++;
            c = input.charAt(now);
            tmp.append(c);
        } else if (c=='>'&&hasNext&& input.charAt(now+1)=='=') {
            now++;
            c = input.charAt(now);
            tmp.append(c);
        } else if (c=='='&&hasNext&& input.charAt(now+1)=='=') {
            now++;
            c = input.charAt(now);
            tmp.append(c);
        }
        result = tmp.toString();
        type = tokenMap.get(result);

        Token token = new Token(type,result,lineNum);
        tokenList.add(token);
        now++;
    }

    public void writeFile(List<Token> tokenList) throws IOException{
        FileWriter fileWriter = new FileWriter("lexer.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        tokenList.forEach(i ->
        {
            System.out.println(i.type+" "+i.token);
            printWriter.println(i.type + " " + i.token);
        });
        printWriter.close();
    }



}
