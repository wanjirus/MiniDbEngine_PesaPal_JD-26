package com.stan.pesapal.engine.sql.commands;

import com.stan.pesapal.engine.schema.Field;
import com.stan.pesapal.engine.sql.SqlStatement;

import java.util.List;

public class CreateTableCommand implements SqlStatement {

    private final String table;
    private final List<Field> columns;

    public CreateTableCommand(String table, List<Field> columns) {
        this.table = table;
        this.columns = columns;
    }

    public String table() {
        return table;
    }

    public List<Field> columns() {
        return columns;
    }
}
