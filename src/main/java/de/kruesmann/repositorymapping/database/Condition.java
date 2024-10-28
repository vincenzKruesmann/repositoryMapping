package de.kruesmann.repositorymapping.database;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Condition {
    private String condition;
    private final List<Object> values = new ArrayList<>();
    private Condition(){}

    public static <T> Condition init(String table, String key, T value){
        Condition condition = new Condition();
        condition.condition = table + "." + key + " = ? ";
        condition.getValues().add(value);
        return condition;
    }

    public static <T> Condition empty(){
        return new Condition();
    }

    public<T> Condition and(String table, String key, T value){
        condition += "AND " + table + "." + key + " = ? ";
        values.add(value);
        return this;
    }

    public <T> Condition or(String table, String key, T value){
        condition += "OR " + table + "." + key + " = ? ";
        values.add(value);
        return this;
    }

    public <T> Condition orNot(String table, String key, T value){
        condition += "OR NOT " + table + "." + key + " = ? ";
        values.add(value);
        return this;
    }

    public <T> Condition andNot(String table, String key, T value){
        condition += "AND NOT " + table + "." + key + " = ?";
        values.add(value);
        return this;
    }

    public Condition clip(Condition condition){
        this.condition += "( " + condition.getCondition() +") ";
        return this;
    }
}
