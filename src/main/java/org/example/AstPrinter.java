package org.example;

public class AstPrinter implements Expr.Visitor<String>{
    String print(Expr expr){
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr){
        return parenthesize(expr.operator.lexme,expr.left,expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr){
        return parenthesize("Group",expr.expresiion);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr){
        if(expr.value==null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr){
        return parenthesize(expr.operator.lexme,expr.right);
    }

    private String parenthesize(String name , Expr ... exprs){
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs){
            builder.append("");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(new Token(tokenType.TokenType.MINUS,"-",null,1),new Expr.Literal(123)),
                new Token(tokenType.TokenType.STAR,"*",null,1), new Expr.Grouping( new Expr.Literal(45.67))
        );

        System.out.println(new AstPrinter().print(expression));
    }
}
