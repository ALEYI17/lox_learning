package org.example;

import java.util.List;

public class LoxFunction implements LoxCallable{
    private final  Stmt.Function declaration;
    private final Enviroment closure;

    private final boolean isInitializer;
    LoxFunction(Stmt.Function declaration,Enviroment closure, Boolean isInitializer){
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer=isInitializer;
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
            if(isInitializer ) return  closure.getAt(0,"this");
            return returnValue.value;
        }
        if(isInitializer ) return  closure.getAt(0,"this");
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
        return  new LoxFunction(declaration,enviroment,isInitializer);
    }
}
