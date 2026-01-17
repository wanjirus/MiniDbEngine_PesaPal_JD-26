package com.stan.pesapal.repl;

public class ReplJoinTestRunner {

    public static void run(Repl repl) {
        System.out.println("=== JOIN TESTS BEGIN ===");

        // --- Setup tables ---
        repl.executeLine("""
            CREATE TABLE users (
                id INT PRIMARY KEY,
                name TEXT
            )
        """);

        repl.executeLine("""
            CREATE TABLE bets (
                id INT PRIMARY KEY,
                user_id INT,
                amount INT
            )
        """);

        // --- Insert users ---
        repl.executeLine("INSERT INTO users VALUES (1, 'Alice')");
        repl.executeLine("INSERT INTO users VALUES (2, 'Bob')");
        repl.executeLine("INSERT INTO users VALUES (3, 'Charlie')");

        // --- Insert bets ---
        repl.executeLine("INSERT INTO bets VALUES (10, 1, 50)");
        repl.executeLine("INSERT INTO bets VALUES (11, 1, 20)");
        repl.executeLine("INSERT INTO bets VALUES (12, 3, 100)");

        // ============================
        // TEST 1 — INNER JOIN
        // ============================
        System.out.println("\n--- INNER JOIN test ---");
        repl.executeLine("""
            SELECT * FROM users
            INNER JOIN bets ON users.id = bets.user_id
        """);

        /*
        EXPECTED:
        Alice rows (2 bets)
        Charlie row (1 bet)
        Bob excluded
        */

        // ============================
        // TEST 2 — LEFT JOIN
        // ============================
        System.out.println("\n--- LEFT JOIN test ---");
        repl.executeLine("""
            SELECT * FROM users
            LEFT JOIN bets ON users.id = bets.user_id
        """);

        /*
        EXPECTED:
        Alice (2 rows)
        Charlie (1 row)
        Bob (1 row, bet columns = null)
        */

        // ============================
        // TEST 3 — JOIN + WHERE
        // ============================
        System.out.println("\n--- JOIN + WHERE test ---");
        repl.executeLine("""
            SELECT * FROM users
            INNER JOIN bets ON users.id = bets.user_id
            WHERE amount >= 50
        """);

        /*
        EXPECTED:
        Alice (50)
        Charlie (100)
        */

        System.out.println("\n=== JOIN TESTS END ===");
        System.out.println("You may now type commands manually.\n");
    }
}