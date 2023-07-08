package org.example;
import java.util.List;

import static org.example.tokenType.TokenType.*;

public class Parser {
    private static class  ParseError extends RuntimeException{}
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    private Expr expression(){
        return equality();
    }

    private  Expr equality(){
        Expr expr = comparison();

        while (match(BANG_EQUAL,EQUAL_EQUAL)){
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }

    private boolean match(tokenType.TokenType ... types){
        for (tokenType.TokenType tok : types){
            if(check(tok)){
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(tokenType.TokenType type){
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd(){
        return peek().type == EOF;
    }

    private Token peek(){
        return tokens.get(current);
    }

    private Token previous(){
        return tokens.get(current -1);
    }

    private Expr comparison(){
        Expr expr = term();

        while ( match(GREATER,GREATER_EQUAL,LESS,LESS_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr term(){
        Expr expr = factor();

        while (match(MINUS,PLUS)){
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr factor(){
        Expr expr = unary();

        while (match(SLASH,STAR)){
            Token operator = previous();
            Expr right =  unary();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr unary(){
        if( match(BANG, MINUS)){
            Token operator  = previous();
            Expr right = unary();
            return new Expr.Unary(operator,right);
        }
        return primary();
    }

    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);

        if(match(NUMBER,STRING)) {
            return new Expr.Literal(previous().literal);
        }
        if (match(LEFT_PAREN)){
            Expr expr = expression();
            consume(RIGHT_PAREN,"Expecteed ')'  after expression");
            return new Expr.Grouping(expr);
        }

        if (match(MINUS, PLUS, SLASH, STAR, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = unary();
            lox.error(operator, "Binary operator without left-hand operand");
            return new Expr.Binary(null, operator, right);
        }

        throw  error(peek(), "Expected  expression");
    }

    private Token consume(tokenType.TokenType type , String message){
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message){
        lox.error(token,message);
        return new ParseError();
    }

    private void  syncronize(){
        advance();

        while (!isAtEnd()){
            if (previous().type == SEMICOLON) return;

            switch (peek().type){
                case CLASS:
                case FUN:
                case LET:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                return;
            }
            advance();
        }
    }

    Expr parse(){
        try {
            return expression();
        }
        catch (ParseError error){
            return  null;
        }
    }
}
