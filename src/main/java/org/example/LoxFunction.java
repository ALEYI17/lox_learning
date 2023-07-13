package org.example;

import java.util.List;

public class LoxFunction implements LoxCallable{
    private final  Stmt.Function declaration;
    private final Enviroment closure;
    LoxFunction(Stmt.Function declaration,Enviroment closure){
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Enviroment enviroment = new Enviroment(closure);

        for( int i = 0 ; i < declaration.params.size(); i ++){
            enviroment.define(declaration.params.get(i).lexme,arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body,enviroment);
        }catch (Return returnValue){
            return returnValue.value;
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<fn" + declaration.name.lexme + ">";
    }

    LoxFunction bind(LoxInstance instance){
        Enviroment enviroment = new Enviroment(closure);
        enviroment.define("this",instance);
        return  new LoxFunction(declaration,enviroment);
    }
}
