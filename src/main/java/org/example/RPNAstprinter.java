package org.example;

public class RPNAstprinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return null;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.left.accept(this) + " " + expr.right.accept(this) + " " + expr.operator.lexme);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        return null;
    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        return null;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return  parenthesize("Group ",expr.expresiion);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitlogicalExpr(Expr.logical expr) {
        return null;
    }

    @Override
    public String visitSetExpr(Expr.Set expr) {
        return null;
    }

    @Override
    public String visitThisExpr(Expr.This expr) {
        return null;
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.right.accept(this) + " " + expr.operator.lexme;
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return null;
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
                new Expr.Unary(new Token(tokenType.TokenType.MINUS, "-", null, 1), new Expr.Literal(123)),
                new Token(tokenType.TokenType.STAR, "*", null, 1),
                new Expr.Grouping(new Expr.Literal(45.67))
        );

        System.out.println(new RPNAstprinter().print(expression));
    }
}