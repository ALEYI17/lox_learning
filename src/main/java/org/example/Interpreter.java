package org.example;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object> , Stmt.Visitor<Void>{
    @Override
    public Object visitLiteralExpr(Expr.Literal expr){
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expresiion);
    }

    private Object evaluate(Expr expr){
        return expr.accept(this);
    }

    private void execute(Stmt stmt){
        stmt.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right =  evaluate(expr.right);

        switch (expr.operator.type){
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator,right);
                return -(double)right;
        }
        return null;
    }

    private boolean isTruthy(Object object){
        if (object == null) return false;
        if( object instanceof Boolean) return (Boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b){
        if (a == null &&  b == null) return true;
        if (a == null) return  false;

        return a.equals(b);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type){
            case GREATER:
                checkNumberOperand(expr.operator,left,right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperand(expr.operator,left,right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperand(expr.operator,left,right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperand(expr.operator,left,right);
                return (double) left <= (double) right;
            case MINUS:
                checkNumberOperand(expr.operator,left,right);
                return (double)left - (double) right;
            case PLUS:
                if(left instanceof Double && right instanceof  Double){
                    return (double)left + (double) right;
                }
                if(left instanceof String && right instanceof  String){
                    return (String) left + (String) right;
                }
                if(left instanceof Double && right instanceof String ){
                    return (String) stringify(left) + (String) right;
                } else if (left instanceof String && right instanceof Double) {
                    return (String) left + (String) stringify(right);
                }
                throw  new RuntimeError(expr.operator,"Operands must be Number or String");
            case SLASH:
                checkNumberOperand(expr.operator,left,right);
                return (double)left / (double) right;
            case STAR:
                checkNumberOperand(expr.operator,left,right);
                return (double)left + (double) right;
            case BANG_EQUAL:return  !isEqual(left,right);
            case EQUAL_EQUAL:return  isEqual(left,right);
        }
        return null;
    }

    private void checkNumberOperand(Token operator , Object operand){
        if (operand instanceof  Double) return;
        throw new  RuntimeError(operator, "Operand mut be a number");
    }

    private void checkNumberOperand(Token operator , Object left,Object right){
        if(operator.type == tokenType.TokenType.SLASH){
            if(right instanceof Double){
                if((double) right == 0) throw new RuntimeError(operator,"Right operand is 0 , and cant divide by 0");
            }
        }
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator,"Operand must be numbers");
    }

    void interpret( List<Stmt> statements){
        try {
            for (Stmt statement: statements){
                execute(statement);
            }
        }catch (RuntimeError e){
            lox.runtimeError(e);
        }
    }

    private String stringify(Object object){
        if(object == null) return "nil";

        if( object instanceof  Double){
            String text = object.toString();
            if (text.endsWith(".0")){
                text = text.substring(0,text.length() -2);
            }
            return text;
        }
        return object.toString();
    }
}
