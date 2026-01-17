MiniDatabase+WebApp Challenge

In this project I built:

            A mini relational database management system (RDBMS) from scratch in Java, featuring a SQL-like interface and interactive REPL

            A mini jackpot betting application using the RDBMS as its store.


Technologies Used

            Java 16

            Maven

            Custom in-memory RDBMS engine

            SQL-like parser and executor

            Interactive REPL

            Spring Boot (REST API)

            Swagger (API documentation)


Features
            RDBMS

            Table creation with basic data types and constraints

            Full CRUD operations with indexing and joins

            SQL-like REPL with filtering and automated tests

Jackpot App

            User accounts, betting rounds, and wagers

            Auditable ledger with real-time balance calculation

            Auto-bootstrapped schema and REST API with Swagger UI

Installation and Setup

            Prerequisites

            Java 16+
            Maven

            NB:---> Open/Run each Project individually

            mvn clean install    -- on each project in the folder individually starting with rdbms.

            mvn spring-boot:run    --for jackpot_app


            For database interaction, REPL starts automatically. 

            Jackpot API runs on http://localhost:8080

            Swagger UI available at:

            http://localhost:8080/swagger-ui/index.html

Testing

            To run various predefined tests that describe the core functionalites of the RDBMS, uncomment this lines in Class Main.Java in rdbms


            //        ReplJoinTestRunner.run(repl);
            //        ReplTestRunner.run(repl);

            or checkout test1.txt file for commands to interact with repl


            In the jackpot app the Ledger entries provide full traceability of all transactions, upholding accountability.
            can be tested through http://localhost:8080/swagger-ui/index.html