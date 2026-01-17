package com.stan.pesapal.engine.index;
import com.stan.pesapal.engine.schema.Row;

import java.util.HashMap;
import java.util.Map;
public class UniqueHashIndex {



        private final String column;
        private final Map<Object, Row> map = new HashMap<>();

        public UniqueHashIndex(String column) {
            this.column = column;
        }

        public void add(Row row) {
            Object key = row.get(column);
            if (map.containsKey(key)) {
                throw new RuntimeException("Duplicate value for UNIQUE index on " + column);
            }
            map.put(key, row);
        }

        public Row get(Object value) {
            return map.get(value);
        }

        public void remove(Row row) {
            map.remove(row.get(column));
        }

}
