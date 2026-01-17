package com.stan.pesapal.engine.sql.commands;
import com.stan.pesapal.engine.sql.JoinClause;
import com.stan.pesapal.engine.sql.SqlStatement;
import com.stan.pesapal.engine.sql.WhereClause;

import java.util.List;

public class SelectCommand implements SqlStatement {

    private final List<String> columns;
    private final String table;
    private final JoinClause join;
    private final WhereClause where;

    // OLD constructor (keep it)
    public SelectCommand(List<String> columns, String table, WhereClause where) {
        this(columns, table, null, where);
    }

    // NEW constructor
    public SelectCommand(
            List<String> columns,
            String table,
            JoinClause join,
            WhereClause where
    ) {
        this.columns = columns;
        this.table = table;
        this.join = join;
        this.where = where;
    }

    public List<String> columns() { return columns; }
    public String table() { return table; }
    public JoinClause join() { return join; }
    public WhereClause where() { return where; }
}
