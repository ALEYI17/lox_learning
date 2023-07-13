package org.example;

public class LoxInstance {
    private  LoxClass klass;

    LoxInstance(LoxClass loxClass){
        this.klass = loxClass;
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
