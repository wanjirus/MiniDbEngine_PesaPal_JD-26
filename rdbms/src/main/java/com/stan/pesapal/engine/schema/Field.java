package com.stan.pesapal.engine.schema;

public class Field {
        public enum Type {
            INT, TEXT, BOOL,DECIMAL
         }

        private final String name;
        private final Type type;
        private final boolean primaryKey;
        private final boolean unique;

        public Field(String name, Type type, boolean primaryKey, boolean unique) {
            this.name = name;
            this.type = type;
            this.primaryKey = primaryKey;
            this.unique = unique || primaryKey;
        }

        public String getName() { return name; }
        public Type getType() { return type; }
        public boolean isPrimaryKey() { return primaryKey; }
        public boolean isUnique() { return unique; }
    }
