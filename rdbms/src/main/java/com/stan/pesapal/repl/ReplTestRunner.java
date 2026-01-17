package com.stan.pesapal.repl;

public class ReplTestRunner {

    public static void run(Repl repl) {

        String[] script = {
                // CREATE
                "CREATE TABLE users (id INT PRIMARY KEY, name TEXT, age INT)",

                // INSERT
                "INSERT INTO users VALUES (1, 'Alice', 25)",
                "INSERT INTO users VALUES (2, 'Bob', 17)",
                "INSERT INTO users VALUES (3, 'Charlie', 30)",

                // SELECT
                "SELECT * FROM users",
                "SELECT * FROM users WHERE age >= 18",
                "SELECT * FROM users WHERE age >= 18 AND name != 'Bob'",
                "SELECT * FROM users WHERE age < 20 OR name = 'Charlie'",

                // UPDATE
                "UPDATE users SET age = 100 WHERE id = 1",
                "SELECT * FROM users",

                "UPDATE users SET age = age + 1 WHERE age >= 30",
                "SELECT * FROM users",

                // DELETE
                "DELETE FROM users WHERE name = 'Bob'",
                "SELECT * FROM users"
        };

        System.out.println("=== RUNNING AUTO TEST SCRIPT ===");

        for (String sql : script) {
            System.out.println("\ndb> " + sql);
            repl.executeLine(sql);
        }

        System.out.println("\n=== SCRIPT COMPLETE — INTERACTIVE MODE ===\n");
    }
}
