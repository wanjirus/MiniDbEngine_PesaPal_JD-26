package com.stan.pesapal.engine.sql;

import java.util.List;

public class WhereClause {
    private final List<Condition> conditions;
    private final String logic; // AND / OR

    public WhereClause(List<Condition> conditions, String logic) {
        this.conditions = conditions;
        this.logic = logic.toUpperCase();
    }

    public List<Condition> conditions() { return conditions; }
    public String logic() { return logic; }
}
