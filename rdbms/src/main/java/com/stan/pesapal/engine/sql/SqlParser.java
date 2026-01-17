package com.stan.pesapal.engine.sql;

import com.stan.pesapal.engine.schema.Field;
import com.stan.pesapal.engine.schema.JoinType;
import com.stan.pesapal.engine.sql.commands.*;

import java.util.*;

public class SqlParser {

    public static SqlStatement parse(String sql) {
        sql = sql.trim();
        String upper = sql.toUpperCase();

        if (upper.startsWith("CREATE TABLE")) return parseCreate(sql);
        if (upper.startsWith("INSERT INTO")) return parseInsert(sql);
        if (upper.startsWith("SELECT")) return parseSelect(sql);
        if (upper.startsWith("UPDATE")) return parseUpdate(sql);
        if (upper.startsWith("DELETE FROM")) return parseDelete(sql);

        throw new RuntimeException("Unsupported SQL");
    }

    // parsing values
    private static Object parseValue(String v) {
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

    // insert
    private static SqlStatement parseInsert(String sql) {
        String[] parts = sql.split("VALUES", 2);
        String table = parts[0].replace("INSERT INTO", "").trim();
        String values = parts[1].trim();
        values = values.substring(1, values.length() - 1); // remove ( )


        List<String> rawValues = splitCsv(values);
        List<Object> parsedValues = new ArrayList<>();
        for (String v : rawValues) parsedValues.add(parseValue(v));

        return new InsertCommand(table, parsedValues);
    }

    private static List<String> splitCsv(String s) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : s.toCharArray()) {
            if (c == '\'') inQuotes = !inQuotes;
            if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result;
    }

    // where clause
    private static WhereClause parseWhere(String conditionStr) {
        String logic;
        String[] condParts;

        if (conditionStr.toUpperCase().contains(" OR ")) {
            logic = "OR";
            condParts = conditionStr.split("(?i)\\s+OR\\s+");
        } else {
            logic = "AND";
            condParts = conditionStr.split("(?i)\\s+AND\\s+");
        }

        List<Condition> conditions = new ArrayList<>();
        for (String c : condParts) {
            String[] p = c.trim().split("\\s+", 3); // col, op, val
            if (p.length < 3) throw new RuntimeException("Invalid WHERE condition: " + c);
            Object val = parseValue(p[2]);
            conditions.add(new Condition(p[0], p[1], val)); // store Object, not String
        }

        return new WhereClause(conditions, logic);
    }

    // create clause
    private static SqlStatement parseCreate(String sql) {
        String body = sql.substring("CREATE TABLE".length()).trim();
        String table = body.substring(0, body.indexOf("(")).trim();
        String cols = body.substring(body.indexOf("(") + 1, body.lastIndexOf(")"));

        List<Field> columns = new ArrayList<>();
        for (String col : cols.split(",")) {
            String[] parts = col.trim().split("\\s+");
            String name = parts[0];
            Field.Type type = Field.Type.valueOf(parts[1].toUpperCase());
            boolean pk = col.toUpperCase().contains("PRIMARY KEY");
            boolean unique = col.toUpperCase().contains("UNIQUE");
            columns.add(new Field(name, type, pk, unique));
        }

        return new CreateTableCommand(table, columns);
    }

    // select
    private static SqlStatement parseSelect(String sql) {
        String upper = sql.toUpperCase();
        String selectPart = sql.substring(6, upper.indexOf("FROM")).trim();
        List<String> columns = selectPart.equals("*")
                ? List.of("*")
                : Arrays.stream(selectPart.split(",")).map(String::trim).toList();

        String rest = sql.substring(upper.indexOf("FROM") + 4).trim();
        String table;
        JoinClause join = null;
        WhereClause where = null;

        // where
        if (rest.toUpperCase().contains("WHERE")) {
            int idx = rest.toUpperCase().indexOf("WHERE");
            where = parseWhere(rest.substring(idx + "WHERE".length()).trim());
            rest = rest.substring(0, idx).trim();
        }

        // join

        String restUpper = rest.toUpperCase();
        if (restUpper.contains(" LEFT JOIN ")) {
            int idx = restUpper.indexOf(" LEFT JOIN ");
            table = rest.substring(0, idx).trim();
            join = parseJoin(rest.substring(idx + " LEFT JOIN ".length()), JoinType.LEFT);
        } else if (restUpper.contains(" INNER JOIN ")) {
            int idx = restUpper.indexOf(" INNER JOIN ");
            table = rest.substring(0, idx).trim();
            join = parseJoin(rest.substring(idx + " INNER JOIN ".length()), JoinType.INNER);
        } else {
            table = rest.trim();
        }

        return new SelectCommand(columns, table, join, where);
    }

    private static JoinClause parseJoin(String joinPart, JoinType type) {
        int onIdx = joinPart.toUpperCase().indexOf(" ON ");
        String rightTable = joinPart.substring(0, onIdx).trim();
        String onClause = joinPart.substring(onIdx + " ON ".length()).trim();
        String[] cond = onClause.split("=");

        String leftCol = cond[0].contains(".") ? cond[0].split("\\.")[1].trim() : cond[0].trim();
        String rightCol = cond[1].contains(".") ? cond[1].split("\\.")[1].trim() : cond[1].trim();

        return new JoinClause(type, rightTable, leftCol, rightCol);
    }

    // update clause
    private static SqlStatement parseUpdate(String sql) {
        String body = sql.substring("UPDATE".length()).trim();
        int setIndex = body.toUpperCase().indexOf("SET");
        if (setIndex == -1) throw new RuntimeException("Missing SET in UPDATE");

        String table = body.substring(0, setIndex).trim();
        String setPart = body.substring(setIndex + 3).trim();

        String column, expr;
        WhereClause where = null;

        if (setPart.toUpperCase().contains("WHERE")) {
            String[] parts = setPart.split("(?i)WHERE", 2);
            String[] setParts = parts[0].trim().split("=", 2);
            column = setParts[0].trim();
            expr = setParts[1].trim();
            where = parseWhere(parts[1].trim());
        } else {
            String[] setParts = setPart.split("=", 2);
            column = setParts[0].trim();
            expr = setParts[1].trim();
        }

        return new UpdateCommand(table, column, expr, where);
    }

    // delete clause
    private static SqlStatement parseDelete(String sql) {
        String rest = sql.substring("DELETE FROM".length()).trim();
        String upper = rest.toUpperCase();
        String table;
        WhereClause where = null;

        if (upper.contains("WHERE")) {
            table = rest.substring(0, upper.indexOf("WHERE")).trim();
            where = parseWhere(rest.substring(upper.indexOf("WHERE") + 5).trim());
        } else {
            table = rest.trim();
        }

        return new DeleteCommand(table, where);
    }
}
