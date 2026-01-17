package com.stan.pesapal.engine.sql.commands;

import com.stan.pesapal.engine.sql.SqlStatement;
import com.stan.pesapal.engine.sql.WhereClause;

import java.util.List;

public class UpdateCommand implements SqlStatement {
    private final String table;
    private final String column;    // Column to update
    private final String expression; // Expression string, e.g., "age + 1"
    private final WhereClause where;

    public UpdateCommand(String table, String column, String expression, WhereClause where) {
        this.table = table;
        this.column = column;
        this.expression = expression;
        this.where = where;
    }

    public String table() { return table; }
    public String column() { return column; }
    public String expression() { return expression; }
    public WhereClause where() { return where; }
}

