package org.example;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance {
    private final Map<String,Object> fields =  new HashMap<>();
    private  LoxClass klass;

    LoxInstance(LoxClass loxClass){
        this.klass = loxClass;
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }

    Object get(Token name){
        if( fields.containsKey(name.lexme)){
            return fields.get(name.lexme);
        }

        throw new RuntimeError(name,"Undefined property " + name.lexme+ ".");
    }
}
