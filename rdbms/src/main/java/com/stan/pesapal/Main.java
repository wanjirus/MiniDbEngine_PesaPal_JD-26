package com.stan.pesapal;

import com.stan.pesapal.engine.schema.Database;
import com.stan.pesapal.repl.Repl;
import com.stan.pesapal.repl.ReplJoinTestRunner;
import com.stan.pesapal.repl.ReplTestRunner;

public class Main {

    public static void main(String[] args) {

        Database database = new Database();
        Repl repl = new Repl(database);
//
//        ReplJoinTestRunner.run(repl);
//        ReplTestRunner.run(repl);
        repl.start();
    }
}
