package com.stan.pesapal.engine.sql;

// Condition.java
public class Condition {
    private final String column;
    private final String operator;
    private final Object value; // <-- change from String to Object

    public Condition(String column, String operator, Object value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    public String column() { return column; }
    public String operator() { return operator; }
    public Object value() { return value; }
}

