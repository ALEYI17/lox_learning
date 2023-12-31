package org.example;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object> , Stmt.Visitor<Void>{
    final Enviroment globals = new Enviroment();
    private  Enviroment enviroment = globals;

    private final Map<Expr,Integer> locals = new HashMap<>();

    Interpreter(){
        globals.define("clock", new LoxCallable(){
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000;
            }

            @Override
            public String toString() {
                return "<Native fn>";
            }
        });
    }
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

    void resolve(Expr expr, int depth){
        locals.put(expr,depth);
    }

    void executeBlock(List<Stmt> statements , Enviroment enviroment){
        Enviroment previous = this.enviroment;

        try {
            this.enviroment = enviroment;

            for(Stmt statement: statements){
                execute(statement);
            }
        }finally {
            this.enviroment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements , new  Enviroment(enviroment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclasss = null;
        if(stmt.superclass != null){
            superclasss = evaluate(stmt.superclass);
            if(! (superclasss instanceof LoxClass)){
                throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
            }
        }
        enviroment.define(stmt.name.lexme,null);

        if(stmt.superclass != null){
            enviroment = new Enviroment(enviroment);
            enviroment.define("super",superclasss);
        }

        Map<String,LoxFunction> methods  = new HashMap<>();
        for(Stmt.Function method: stmt.methods){
            LoxFunction function = new LoxFunction(method,enviroment,method.name.lexme.equals("init"));
        }

        LoxClass klass = new LoxClass(stmt.name.lexme,(LoxClass) superclasss,methods);
        if(superclasss != null){
            enviroment = enviroment.enclosing;
        }
        enviroment.assign(stmt.name,klass);

        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt,enviroment,false);
        enviroment.define(stmt.name.lexme,function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        }
        else if (stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Object visitlogicalExpr(Expr.logical expr) {
        Object left = evaluate(expr.left);

        if(expr.operator.type == tokenType.TokenType.OR){
            if (isTruthy(left)) return  left;
        }else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);

    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object =  evaluate(expr.object);

        if (!(object instanceof  LoxInstance)){
            throw  new RuntimeError(expr.name, "Only instances have fields");
        }

        Object value =  evaluate(expr.value);
        ((LoxInstance) object).set(expr.name,value);
        return value;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        LoxClass  superclass = (LoxClass) enviroment.getAt(distance,"super");
        LoxInstance object = (LoxInstance) enviroment.getAt(distance -1 , "this");
        LoxFunction method = superclass.findMethod(expr.method.lexme);
        if (method  == null){
            throw new RuntimeError(expr.method , "Undefined property'" + expr.method.lexme + "'.");
        }
        return method.bind(object);
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVriable(expr.keyword,expr);
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))){
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if(stmt.value != null) value = evaluate(stmt.value);
        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if(stmt.initializer!= null){
            value = evaluate(stmt.initializer);
        }

        enviroment.define(stmt.name.lexme,value);
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value =  evaluate(expr.value);
        Integer distance =  locals.get(expr);
        if(distance != null){
            enviroment.assignAt(distance,expr.name,value);
        }else {
            globals.assign(expr.name,value);
        }
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVriable(expr.name,expr);
    }

    private  Object lookUpVriable(Token name , Expr expr){
        Integer distance = locals.get(expr);

        if(distance != null){
            return enviroment.getAt(distance , name.lexme);
        }else {
            return globals.get(name);
        }
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

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee =  evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for(Expr argument: expr.arguments){
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof  LoxCallable)){
            throw new RuntimeError(expr.paren,"Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable)callee;

        if(arguments.size() != function.arity()){
            throw new RuntimeError(expr.paren, " Expected " + function.arity() + "arguments but got" + arguments.size() + ".");
        }
        return function.call(this,arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof  LoxInstance){
            return ((LoxInstance) object).get(expr.name);
        }

        throw new RuntimeError(expr.name,"Only instances have properties.");
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
