package com.stan.pesapal.engine.schema;

import java.util.*;

public class Database {

    private  Map<String, Table> tables = new HashMap<>();

    public void createTable(String name, List<Field> columns) {
        if (tables.containsKey(name)) {
            throw new RuntimeException("Table exists: " + name);
        }
        tables.put(name, new Table(name, columns));
    }

    public Table table(String name) {
        Table t = tables.get(name);
        if (t == null) throw new RuntimeException("Unknown table: " + name);
        return t;
    }
}

