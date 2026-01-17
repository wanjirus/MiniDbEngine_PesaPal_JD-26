package com.stan.pesapal.engine.execution;

import com.stan.pesapal.engine.schema.Database;
import com.stan.pesapal.engine.schema.JoinType;
import com.stan.pesapal.engine.schema.Row;
import com.stan.pesapal.engine.schema.Table;
import com.stan.pesapal.engine.sql.*;
import com.stan.pesapal.engine.sql.commands.*;
import com.stan.pesapal.engine.utils.ExpressionEvaluator;

import java.util.*;

public class StatementExecutor {

    private final Database db;

    public StatementExecutor(Database db) {
        this.db = db;
    }

    // ---------- EXECUTE QUERY ----------
    public List<Row> executeQuery(SelectCommand cmd) {
        Table baseTable = db.table(cmd.table());
        List<Row> rows;

        if (cmd.join() != null) {
            rows = executeJoin(baseTable, cmd.join());
        } else {
            rows = baseTable.rows();
        }

        if (cmd.where() != null) {
            rows = applyWhereRows(rows, cmd.where());
        }

        return rows;
    }

    // ---------- EXECUTE STATEMENT ----------
    public void execute(SqlStatement cmd) {

        if (cmd instanceof CreateTableCommand c) {
            db.createTable(c.table(), c.columns());
            System.out.println("OK");
            return;
        }

        if (cmd instanceof SelectCommand s) {
            List<Row> rows = executeQuery(s);
            printResult(s.columns(), rows);
            return;
        }

        if (cmd instanceof InsertCommand i) {
            Table t = db.table(i.table());
            Row r = new Row();

            int idx = 0;
            for (var col : t.columns()) {
                Object val = i.values().get(idx++); // already Object
                r.set(col.getName(), val);
            }

            t.insert(r);
            System.out.println("1 row inserted");
            return;
        }

        if (cmd instanceof DeleteCommand d) {
            executeDelete(d);
            return;
        }

        if (cmd instanceof UpdateCommand u) {
            Table t = db.table(u.table());
            List<Row> targets = (u.where() != null)
                    ? applyWhere(t, u.where())
                    : t.rows();

            for (Row r : targets) {
                Object result = ExpressionEvaluator.evaluate(u.expression(), r);
                Object oldVal = r.get(u.column());

                // Preserve original numeric type
                if (oldVal instanceof Integer && result instanceof Number) {
                    r.set(u.column(), ((Number) result).intValue());
                } else if (oldVal instanceof Long && result instanceof Number) {
                    r.set(u.column(), ((Number) result).longValue());
                } else if (oldVal instanceof Double && result instanceof Number) {
                    r.set(u.column(), ((Number) result).doubleValue());
                } else {
                    r.set(u.column(), result);
                }
            }


            System.out.println(targets.size() + " row(s) updated");
            return;
        }

        throw new RuntimeException("SQL statement not supported");
    }

    // ---------- APPLY WHERE ----------
    private List<Row> applyWhere(Table table, WhereClause where) {
        List<Row> out = new ArrayList<>();
        for (Row r : table.rows()) {
            boolean match = evaluateLogic(r, where);
            if (match) out.add(r);
        }
        return out;
    }

    private List<Row> applyWhereRows(List<Row> rows, WhereClause where) {
        List<Row> out = new ArrayList<>();
        for (Row r : rows) {
            boolean match = evaluateLogic(r, where);
            if (match) out.add(r);
        }
        return out;
    }

    private boolean evaluateLogic(Row r, WhereClause where) {
        if (where.logic().equalsIgnoreCase("AND")) {
            return where.conditions().stream().allMatch(cond -> evaluateCondition(r, cond));
        } else {
            return where.conditions().stream().anyMatch(cond -> evaluateCondition(r, cond));
        }
    }

    private boolean evaluateCondition(Row r, Condition cond) {
        Object rowVal = r.get(cond.column());
        Object condVal = cond.value();

        if (rowVal == null && condVal == null) return true;
        if (rowVal == null || condVal == null) return false;

        String op = cond.operator().toUpperCase();

        switch (op) {
            case "=" -> { return Objects.equals(rowVal, condVal); }
            case "!=" -> { return !Objects.equals(rowVal, condVal); }
            case "<" -> { return compareNumbers(rowVal, condVal) < 0; }
            case "<=" -> { return compareNumbers(rowVal, condVal) <= 0; }
            case ">" -> { return compareNumbers(rowVal, condVal) > 0; }
            case ">=" -> { return compareNumbers(rowVal, condVal) >= 0; }
            case "LIKE" -> { return likeMatch(rowVal.toString(), condVal.toString()); }
            case "IN" -> { return inMatch(rowVal, condVal); }
            default -> throw new RuntimeException("Unsupported operator: " + op);
        }
    }

    private int compareNumbers(Object a, Object b) {
        if (!(a instanceof Number) || !(b instanceof Number)) {
            throw new RuntimeException("Cannot compare non-numeric values");
        }
        return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
    }

    private boolean likeMatch(String value, String pattern) {
        String regex = pattern.replace("%", ".*");
        return value.matches(regex);
    }

    private boolean inMatch(Object rowVal, Object condVal) {
        if (!(condVal instanceof String str)) return false;
        str = str.trim();
        if (str.startsWith("(") && str.endsWith(")")) {
            str = str.substring(1, str.length() - 1);
        }
        for (String part : str.split(",")) {
            Object v = parseValue(part.trim());
            if (Objects.equals(rowVal, v)) return true;
        }
        return false;
    }

    // ---------- PARSE VALUE ----------
    private Object parseValue(String v) {
        v = v.trim();
        if (v.equalsIgnoreCase("NULL")) return null;
        if (v.startsWith("'") && v.endsWith("'")) return v.substring(1, v.length() - 1);
        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) return Boolean.parseBoolean(v);
        try {
            if (v.contains(".")) return Double.parseDouble(v);
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return v; // fallback string
        }
    }

    // ---------- DELETE ----------
    private void executeDelete(DeleteCommand cmd) {
        Table table = db.table(cmd.table());
        List<Row> targets = (cmd.where() != null)
                ? applyWhere(table, cmd.where())
                : new ArrayList<>(table.rows());

        targets.forEach(table.rows()::remove);
        System.out.println(targets.size() + " row(s) deleted");
    }

    // ---------- JOIN ----------
    private List<Row> executeJoin(Table left, JoinClause join) {
        Table right = db.table(join.rightTable());
        List<Row> result = new ArrayList<>();

        String leftCol = join.leftColumn();
        if (leftCol.contains(".")) leftCol = leftCol.split("\\.")[1];

        String rightCol = join.rightColumn();
        if (rightCol.contains(".")) rightCol = rightCol.split("\\.")[1];

        for (Row l : left.rows()) {
            boolean matched = false;

            for (Row r : right.rows()) {
                Object lv = l.get(leftCol);
                Object rv = r.get(rightCol);

                if (Objects.equals(lv, rv)) {
                    Row merged = new Row();
                    merged.values().putAll(l.values());
                    merged.values().putAll(r.values());
                    result.add(merged);
                    matched = true;
                }
            }

            if (!matched && join.type() == JoinType.LEFT) {
                Row merged = new Row();
                merged.values().putAll(l.values());
                for (var col : right.columns()) merged.set(col.getName(), null);
                result.add(merged);
            }
        }

        return result;
    }

    // ---------- PRINT ----------
    private void printResult(List<String> columns, List<Row> rows) {
        if (rows.isEmpty()) {
            System.out.println("(0 rows)");
            return;
        }

        List<String> outputCols =
                columns.size() == 1 && columns.get(0).equals("*")
                        ? new ArrayList<>(rows.get(0).values().keySet())
                        : columns;

        outputCols.forEach(c -> System.out.print(c + "\t"));
        System.out.println();

        for (Row r : rows) {
            for (String c : outputCols) {
                System.out.print(r.get(c) + "\t");
            }
            System.out.println();
        }

        System.out.println(rows.size() + " row(s)");
    }
}
