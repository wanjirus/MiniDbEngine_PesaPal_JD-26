package com.stan.webapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stan.pesapal.engine.schema.Database;
import com.stan.pesapal.repl.Repl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Component
public class DatabasePersistence {

    private final Repl repl;
    private final ObjectMapper objectMapper = new ObjectMapper(); // <-- here

    public DatabasePersistence(Repl repl) {
        this.repl = repl;
    }

    @PostConstruct

    //check for an existing snapshot if not exists start new.
    public void restore() {
        File snapshotFile = new File("db_snapshot.json");

        if (!snapshotFile.exists() || snapshotFile.length() == 0) {
            System.out.println("No snapshot found, starting with empty DB");
            return;
        }

        try {
            Database db = objectMapper.readValue(snapshotFile, Database.class);
            repl.replaceDatabase(db);
            System.out.println("✅ Database restored from snapshot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //creating and saving a snapshot
    public void save() {
        try {
            objectMapper.writeValue(new File("db_snapshot.json"), repl.getDatabase());
            System.out.println("✅ Database saved to snapshot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
