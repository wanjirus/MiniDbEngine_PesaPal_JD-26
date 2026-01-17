package com.stan.pesapal.engine.execution;

import com.stan.pesapal.engine.schema.Database;

import java.util.concurrent.Executor;

//@Component
public class DatabaseContext {
    private Database db;
    private Executor executor;

    public DatabaseContext(Database db, Executor executor) {
        this.db = db;
        this.executor = executor;
    }

    public Database getDb() { return db; }
    public Executor getExecutor() { return executor; }

    public void replace(Database db, Executor executor) {
        this.db = db;
        this.executor = executor;
    }
}
