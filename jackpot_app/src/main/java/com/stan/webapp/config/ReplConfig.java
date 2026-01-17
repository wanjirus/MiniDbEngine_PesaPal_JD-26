package com.stan.webapp.config;

import com.stan.pesapal.engine.schema.Database;
import com.stan.pesapal.repl.Repl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReplConfig {

//    database as a single shared source
    @Bean
    public Database database() {
        return new Database();
    }

    // database injection
    @Bean
    public Repl repl(Database database) {
        return new Repl(database);
    }
}
