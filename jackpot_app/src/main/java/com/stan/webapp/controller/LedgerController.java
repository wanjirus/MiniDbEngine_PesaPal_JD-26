package com.stan.webapp.controller;

import com.stan.pesapal.repl.Repl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ledger")
@Tag(name = "Ledger", description = "Ledger entries & balances")

public class LedgerController {

    private final Repl repl;

    public LedgerController(Repl repl) {
        this.repl = repl;
    }

//  ledger entries from account including real-time calculated balance
    @GetMapping("/{accountId}")
    public Map<String, Object> getAccountLedger(@PathVariable int accountId) {
        List<Map<String, Object>> entries = repl.query(
                "SELECT * FROM ledger_entries WHERE account_id = " + accountId
        );
        double balance = entries.stream()
                .mapToDouble(e -> ((Number) e.get("amount")).doubleValue())
                .sum();

        return Map.of(
                "accountId", accountId,
                "balance", balance,
                "entries", entries
        );
    }

    @GetMapping("/{userId}/betsummary")
    public List<Map<String, Object>> getUserBetsSummary(@PathVariable int userId) {
       List<Map<String, Object>> rows = repl.query(
                "SELECT users.id, users.name, bets.amount " +
                        "FROM users " +
                        "INNER JOIN bets ON users.id = bets.user_id " +
                        "WHERE bets.amount >= 50 AND users.id = " + userId
        );

        if (rows.isEmpty()) {
            return List.of(Map.of("error", "No bets >= 50 for this user"));
        }

        return rows;
    }


    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsersBalances() {
        // Get all accounts that are USER type
        List<Map<String, Object>> accounts = repl.query("SELECT * FROM accounts WHERE type = 'USER'");

        for (Map<String, Object> account : accounts) {
            int accountId = (int) account.get("id");

            double balance = repl.query("SELECT * FROM ledger_entries WHERE account_id = " + accountId)
                    .stream()
                    .mapToDouble(e -> ((Number) e.get("amount")).doubleValue())
                    .sum();

            account.put("balance", balance);
        }

        return accounts;
    }
}
