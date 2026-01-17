package com.stan.pesapal.engine.sql.commands;

import com.stan.pesapal.engine.sql.SqlStatement;
import java.util.List;

public class InsertCommand implements SqlStatement {
    private final String table;
    private final List<Object> values;

    public InsertCommand(String table, List<Object> values) {
        this.table = table;
        this.values = values;
    }

    public String table() {
        return table;
    }

    public List<Object> values() {
        return values;
    }
}
