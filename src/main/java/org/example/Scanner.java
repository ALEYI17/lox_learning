package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.tokenType.TokenType.*;
public class Scanner {
    private final String src;
    private final List<Token> tokens = new ArrayList<>();
    private int start =0;
    private int current =0;
    private int line = 1;
    private static final Map<String, tokenType.TokenType> keyWords;
    static {
        keyWords = new HashMap<>();
        keyWords.put("and",AND);
        keyWords.put("class",CLASS);
        keyWords.put("else",ELSE);
        keyWords.put("fals",FALSE);
        keyWords.put("for",FOR);
        keyWords.put("fn",FUN);
        keyWords.put("if",IF);
        keyWords.put("nil",NIL);
        keyWords.put("or",OR);
        keyWords.put("print",PRINT);
        keyWords.put("return",RETURN);
        keyWords.put("super",SUPER);
        keyWords.put("this",THIS);
        keyWords.put("true",TRUE);
        keyWords.put("let",LET);
        keyWords.put("while",WHILE);
    }

    public Scanner(String src) {
        this.src = src;
    }
    List<Token> scanTokens(){
        while (!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF,"",null,line));
        return tokens;
    }
    private boolean isAtEnd(){
        return current>= src.length();
    }
    private void scanToken(){
        char C = advance();
        switch (C){
            case '(':addToken(LEFT_PAREN);break;
            case ')':addToken(RIGHT_PAREN);break;
            case '{':addToken(LEFT_BRACE);break;
            case '}':addToken(RIGHT_BRACE);break;
            case ',':addToken(COMMA);break;
            case '.':addToken(DOT);break;
            case '-':addToken(MINUS);break;
            case '+':addToken(PLUS);break;
            case ';':addToken(SEMICOLON);break;
            case '*':addToken(STAR);break;
            case '!':addToken(match('=') ? BANG_EQUAL:BANG); break;
            case '=':addToken(match('=') ? EQUAL_EQUAL:EQUAL); break;
            case '<':addToken(match('=') ? LESS:LESS_EQUAL); break;
            case '>':addToken(match('=') ? GREATER:GREATER_EQUAL); break;
            case '/':
                if (match('/')){
                    while (peek()!= '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    while (peek()!= '*' && peekNext() != '/' && isAtEnd()){
                        if(peek()=='\n') line++;
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if(isDigit(C)){
                    number();
                } else if (isAlpha(C)) {
                    identifier();
                } else {
                    lox.error(line,"Unexpected character.");
                }

                break;
        }
    }
    private void identifier(){
        while (isAlphaNumeric(peek())) advance();
        String text = src.substring(start,current);
        tokenType.TokenType type = keyWords.get(text);
        if (type == null) type= IDENTIFIER;
        addToken(type);
    }
    private boolean isAlpha(char c){
        return (c >= 'a' &&  c <= 'z') || (c >= 'A' && c<= 'Z') || c =='_';
    }
    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }
    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }
    private void number(){
        while (isDigit(peek())) advance();
        if(peek()=='.' &&  isDigit((peekNext()))){
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER,Double.parseDouble(src.substring(start,current)));
    }
    private char peekNext(){
        if (current + 1 >= src.length())return '\0';
        return src.charAt(current  +1);
    }
    private void string(){
        while (peek()!= '"' &&  !isAtEnd()){
            if (peek() == '\n') line++;
            advance();
        }
        if(isAtEnd()){
            lox.error(line,"undeterminated strings");
            return;
        }
        advance();
        String value =  src.substring(start+1 ,  current -1);
        addToken(STRING,value);
    }
    private boolean match(char expected){
        if( isAtEnd()) return false;
        if (src.charAt(current)!= expected) return false;
        current++;
        return true;
    }
    private char advance(){
        current++;
        return src.charAt(current-1);
    }
    private void addToken(tokenType.TokenType type){
        addToken(type,null);
    }
    private void addToken(tokenType.TokenType type, Object literal){
        String text = src.substring(start,current);
        tokens.add(new Token(type,text,literal,line));
    }
    private char peek(){
        if( isAtEnd()) return '\0';
        return src.charAt(current);
    }

}
