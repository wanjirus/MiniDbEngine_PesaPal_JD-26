package com.stan.pesapal.engine.schema;

import com.stan.pesapal.engine.index.UniqueHashIndex;

import java.util.*;

public class Table {

    private final String name;
    private final List<Field> columns;
    private final List<Row> rows = new ArrayList<>();
    private  Map<String, UniqueHashIndex> indexes = new HashMap<>();

    public Table(String name, List<Field> columns) {
        this.name = name;
        this.columns = columns;
        createIndexes();
    }

    private void createIndexes() {
        for (Field c : columns) {
            if (c.isPrimaryKey() || c.isUnique()) {
                indexes.put(c.getName(), new UniqueHashIndex(c.getName()));
            }
        }
    }

    public void insert(Row row) {
        validate(row);
        indexes.values().forEach(i -> i.add(row));
        rows.add(row);
    }

    private void validate(Row row) {
        for (Field c : columns) {
            Object v = row.get(c.getName());
            if (v == null) {
                throw new RuntimeException("Missing value for column " + c.getName());
            }
        }
    }

    // 🔑 DATA
    public List<Row> rows() {
        return rows;
    }

    // 🔑 INDEX ACCESS
    public UniqueHashIndex index(String column) {
        return indexes.get(column);
    }

    // 🔑 METADATA
    public String name() {
        return name;
    }

    // ✅ ENGINE-FRIENDLY API
    public List<Field> columns() {
        return columns;
    }

    // Optional JavaBean-style alias (harmless)
    public List<Field> getFields() {
        return columns;
    }
}
