package com.stan.pesapal.engine.sql;

import com.stan.pesapal.engine.schema.JoinType;

public class JoinClause {


    private final JoinType type;
    private final String rightTable;
    private final String leftColumn;
    private final String rightColumn;

    public JoinClause(
            JoinType type,
            String rightTable,
            String leftColumn,
            String rightColumn
    ) {
        this.type = type;
        this.rightTable = rightTable;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    public JoinType type() { return type; }
    public String rightTable() { return rightTable; }
    public String leftColumn() { return leftColumn; }
    public String rightColumn() { return rightColumn; }
}
