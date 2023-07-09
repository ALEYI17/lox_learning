package org.example;
import java.util.HashMap;
import java.util.Map;
public class Enviroment {
    private final Map<String,Object> values = new HashMap<>();

    Object get(Token name){
        if( values.containsKey(name.lexme)){
            return values.get(name.lexme);
        }
        throw new RuntimeError(name,"Undefined variable '" + name.lexme+".");
    }

    void define(String name , Object value){
        values.put(name,value);
    }
}
