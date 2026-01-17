package com.stan.pesapal.engine.sql.commands;

import com.stan.pesapal.engine.sql.SqlStatement;
import com.stan.pesapal.engine.sql.WhereClause;

public class DeleteCommand implements SqlStatement {

    private final String table;
    private final WhereClause where;

    public DeleteCommand(String table, WhereClause where) {
        this.table = table;
        this.where = where;
    }

    public String table() { return table; }
    public WhereClause where() { return where; }
}
