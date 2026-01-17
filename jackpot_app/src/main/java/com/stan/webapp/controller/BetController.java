package com.stan.webapp.controller;

import com.stan.pesapal.repl.Repl;
import com.stan.webapp.dto.PlaceBetRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Bets", description = "Bet placement")
@RequestMapping("/bets")
public class BetController {

    private final Repl repl;

    private static final int JACKPOT_ACCOUNT_ID = 999; // account for jackpot_fund
    private static final int MAINTENANCE_ACCOUNT_ID = 1000; // account for maintenance_fund

    public BetController(Repl repl) {
        this.repl = repl;
    }

    @PostMapping
        public String placeBet(@RequestBody PlaceBetRequest request) {
            int accountId = request.getAccountId();
            int roundId = request.getRoundId();
            double amount = request.getAmount();

        // user balance (derived from ledger)
        double userBalance = repl.query(
                        "SELECT * FROM ledger_entries WHERE account_id = " + accountId
                ).stream()
                .mapToDouble(e -> ((Number) e.get("amount")).doubleValue())
                .sum();

        if (userBalance < amount) {
            return "Insufficient balance for account " + accountId;
        }

        // Debit jackpot amount from user account (ledger)
        repl.executeLine(
                "INSERT INTO ledger_entries VALUES (" +
                        System.currentTimeMillis() + ", " +
                        accountId + ", " +
                        (-amount) + ", " +
                        "'BET_PLACED', " +
                        "'Round_" + roundId + "')"
        );

        // Credit jackpot account in (ledger)
        repl.executeLine(
                "INSERT INTO ledger_entries VALUES (" +
                        (System.currentTimeMillis() + 1) + ", " +
                        JACKPOT_ACCOUNT_ID + ", " +
                        amount + ", " +
                        "'BET_PLACED', " +
                        "'Round_" + roundId + "')"
        );

        // Record bet
        repl.executeLine(
                "INSERT INTO bets VALUES (" +
                        System.currentTimeMillis() + ", " +  // bet id
                        accountId + ", " +
                        amount + ", " +
                        roundId + ")"
        );

        return "✅ Bet placed for account " + accountId + " in round " + roundId;
    }
}
