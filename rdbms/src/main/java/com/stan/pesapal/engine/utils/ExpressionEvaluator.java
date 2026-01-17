package com.stan.pesapal.engine.utils;

import com.stan.pesapal.engine.schema.Row;

public class ExpressionEvaluator {

// handling various expressions
    public static Object evaluate(String expr, Row row) {
        expr = expr.trim();

        try {
            if (expr.contains(".")) return Double.parseDouble(expr);
            return Integer.parseInt(expr);
        } catch (NumberFormatException ignored) {
        }

       if (row.get(expr) != null) return row.get(expr);

        // for binary operations
        String[] ops = {"+", "-", "*", "/"};
        for (String op : ops) {
            if (expr.contains(op)) {
                String[] parts = expr.split("\\" + op);
                String left = parts[0].trim();
                String right = parts[1].trim();

                Object leftVal = row.get(left) != null
                        ? row.get(left)
                        : parseNumber(left);

                Object rightVal = row.get(right) != null
                        ? row.get(right)
                        : parseNumber(right);

                if (!(leftVal instanceof Number) || !(rightVal instanceof Number)) {
                    throw new RuntimeException("Non-numeric expression");
                }

                double lv = ((Number) leftVal).doubleValue();
                double rv = ((Number) rightVal).doubleValue();

                return switch (op) {
                    case "+" -> lv + rv;
                    case "-" -> lv - rv;
                    case "*" -> lv * rv;
                    case "/" -> lv / rv;
                    default -> throw new RuntimeException("Unknown operator");
                };
            }
        }

        throw new RuntimeException("Cannot evaluate expression: " + expr);
    }

    private static Number parseNumber(String v) {
        if (v.contains(".")) return Double.parseDouble(v);
        return Integer.parseInt(v);
    }

}