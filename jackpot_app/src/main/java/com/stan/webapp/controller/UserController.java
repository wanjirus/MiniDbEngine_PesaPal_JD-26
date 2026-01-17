package com.stan.webapp.controller;

import com.stan.pesapal.repl.Repl;
import com.stan.webapp.dto.CreateUserRequest;
import com.stan.webapp.dto.UpdateUserRequest;
import com.stan.webapp.dto.UserDTO;
import com.stan.webapp.dto.UserSummaryDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Repl repl;

    public UserController(Repl repl) {
        this.repl = repl;
    }

    // CREATE USER + ACCOUNT + WITH INITIAL CREDIT

    @Operation(summary = "Create user with optional initial balance")
    @PostMapping
    public String createUser(@RequestBody CreateUserRequest req) {

        repl.executeLine(
                "INSERT INTO users VALUES ("
                        + req.id + ", '"
                        + req.name.replace("'", "''") + "', "
                        + req.age +
                        ")"
        );

        repl.executeLine(
                "INSERT INTO accounts VALUES (" +
                        req.id + ", 'USER', " + req.id + ", 'USER_" + req.name + "')"
        );

        if (req.balance > 0) {
            repl.executeLine(
                    "INSERT INTO ledger_entries VALUES (" +
                            System.currentTimeMillis() + ", " +
                            req.id + ", " +
                            req.balance + ", 'INITIAL_CREDIT', 'USER_CREATE')"
            );
        }

        return "✅ User created with account";
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return repl.query("SELECT * FROM users")
                .stream()
                .map(r -> new UserDTO((int) r.get("id"), (String) r.get("name"), (int) r.get("age")))
                .toList();
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable int id) {
        List<UserDTO> users = repl.query("SELECT * FROM users WHERE id = " + id)
                .stream()
                .map(r -> new UserDTO((int) r.get("id"), (String) r.get("name"), (int) r.get("age")))
                .toList();

        if (users.isEmpty()) return null; // or throw 404
        return users.get(0);
    }

    @Operation(
            summary = "Get user balance",
            description = "Returns real-time balance derived from ledger entries (not stored)"
    )
    @GetMapping("/{id}/balance")

    public double getBalance(@PathVariable int id) {

        List<Map<String, Object>> rows =
                repl.query(
                        "SELECT amount FROM ledger_entries WHERE account_id = " + id
                );

        return rows.stream()
                .mapToDouble(r -> ((Number) r.get("amount")).doubleValue())
                .sum();
    }

    @Operation(summary = "Update a user's age by id")
    @PutMapping("/{id}")
    public String updateUser(@PathVariable int id, @RequestBody UpdateUserRequest req) {

        Integer age = req.getAge();

        repl.executeLine(
                "UPDATE users SET age = " + age + " WHERE id = " + id
        );

        return "✅ User " + id + " updated";
    }

    @Operation(summary = "Delete a user by id which intern deletes the account")
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        repl.executeLine(
                "DELETE FROM users WHERE id = " + id
        );

        repl.executeLine(
                "DELETE FROM accounts WHERE user_id = " + id
        );

        return "✅ User " + id + " deleted";
    }
}

