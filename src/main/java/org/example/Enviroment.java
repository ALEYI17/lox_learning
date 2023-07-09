package org.example;
import java.util.HashMap;
import java.util.Map;
public class Enviroment {
    final  Enviroment enclosing;
    private final Map<String,Object> values = new HashMap<>();

    Enviroment(){
        enclosing = null;
    }

    Enviroment(Enviroment enclosing){
        this.enclosing = enclosing;
    }

    Object get(Token name){
        if( values.containsKey(name.lexme)){
            return values.get(name.lexme);
        }
        if(enclosing != null) return enclosing.get(name);
        throw new RuntimeError(name,"Undefined variable '" + name.lexme+".");
    }

    void define(String name , Object value){
        values.put(name,value);
    }

    void assign(Token name, Object value){
        if(values.containsKey(name.lexme)){
            values.put(name.lexme,value);
            return;
        }
        if(enclosing != null){
            enclosing.assign(name,value);
            return;
        }
        throw new RuntimeError(name,"undefined variable'" + name.lexme+"'.");
    }
}
