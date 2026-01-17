package com.stan.webapp.service;

import com.stan.pesapal.repl.Repl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapService implements CommandLineRunner {

    private final Repl repl;

    public BootstrapService(Repl repl) {
        this.repl = repl;
    }

    @Override
    public void run(String... args) {
        repl.executeLine("CREATE TABLE users (id INT, name TEXT, age INT)");
        repl.executeLine("CREATE TABLE accounts (id INT, type TEXT, user_id INT)");
        repl.executeLine("CREATE TABLE ledger_entries (id INT, account_id INT, amount DECIMAL, type TEXT)");
        repl.executeLine("CREATE TABLE rounds (id INT, status TEXT)");
        repl.executeLine("CREATE TABLE bets (id INT, account_id INT, amount DECIMAL, round_id INT)");
        repl.executeLine("INSERT INTO rounds VALUES (1, 'OPEN');");
        System.out.println("------------ Schema bootstrapped------------");
    }
}
