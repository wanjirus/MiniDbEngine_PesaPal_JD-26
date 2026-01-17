package com.stan.pesapal.repl;

import com.stan.pesapal.engine.execution.StatementExecutor;
import com.stan.pesapal.engine.schema.Database;
import com.stan.pesapal.engine.schema.Row;
import com.stan.pesapal.engine.sql.SqlParser;
import com.stan.pesapal.engine.sql.SqlStatement;

import java.util.*;

public class Repl {

    private Database db;
    private StatementExecutor executor;

    public Repl(Database db) {
        this.db = db;
        this.executor = new StatementExecutor(db);
    }

    public void start() {
        Scanner sc = new Scanner(System.in);
        StringBuilder buffer = new StringBuilder();

        // Welcome message
        System.out.println("WELCOME!");
        System.out.println("Type your SQL commands. Type 'exit;' to quit.\n");

        System.out.print("stan_rdbms> ");

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.trim().equalsIgnoreCase("exit") || line.trim().equalsIgnoreCase("exit;")) {
                System.out.println("BYE.");
                break;
            }

            buffer.append(line).append("\n");
            String content = buffer.toString();

            while (content.contains(";")) {
                int idx = content.indexOf(";");
                String sql = content.substring(0, idx).trim();
                content = content.substring(idx + 1);

                if (!sql.isEmpty()) {
                    try {
                        SqlStatement stmt = SqlParser.parse(sql);
                        executor.execute(stmt);
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    } finally {
                        buffer.setLength(0);
                        buffer.append(content);
                        System.out.print("stan_rdbms> ");
                    }
                }
            }

            buffer.setLength(0);
            buffer.append(content);
            System.out.print("stan_rdbms> ");
        }
    }

    public void executeLine(String sql) {
        try {
            SqlStatement stmt = SqlParser.parse(sql);
            executor.execute(stmt);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> query(String sql) {
        SqlStatement stmt = SqlParser.parse(sql);

        if (!(stmt instanceof com.stan.pesapal.engine.sql.commands.SelectCommand select)) {
            throw new RuntimeException("Only SELECT queries supported in query()");
        }

        List<Row> rows = executor.executeQuery(select);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Row r : rows) {
            result.add(new HashMap<>(r.values()));
        }

        return result;
    }

    public Database getDatabase() {
        return db;
    }

    public void replaceDatabase(Database newDb) {
        this.db = newDb;
        this.executor = new StatementExecutor(newDb);
    }
}
