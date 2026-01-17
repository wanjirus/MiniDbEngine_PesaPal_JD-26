package com.stan.webapp.controller;

import com.stan.pesapal.repl.Repl;
import com.stan.webapp.dto.CloseRoundResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rounds")
@Tag(name = "Rounds", description = "Round lifecycle & payouts")

public class RoundController {

    private final Repl repl;

    public RoundController(Repl repl) {
        this.repl = repl;
    }

    @PostMapping("/close/{roundId}")
    public CloseRoundResponse closeRound(@PathVariable int roundId) {
        List<Map<String, Object>> bets = repl.query("SELECT * FROM bets WHERE round_id = " + roundId);
        if (bets.isEmpty()) return new CloseRoundResponse(roundId, List.of(), 0);

        double totalJackpot = bets.stream()
                .mapToDouble(b -> ((Number) b.get("amount")).doubleValue())
                .sum();

        double payoutPool = totalJackpot * 0.8;
        double maintenanceCut = totalJackpot * 0.2;

        // Pick 2 winners
        List<Map<String, Object>> winnersMap = new ArrayList<>();
        if (bets.size() <= 2) {
            winnersMap.addAll(bets);
        } else {
            Random rnd = new Random();
            Set<Integer> winnerIndexes = new HashSet<>();
            while (winnerIndexes.size() < 2) {
                winnerIndexes.add(rnd.nextInt(bets.size()));
            }
            for (int idx : winnerIndexes) winnersMap.add(bets.get(idx));
        }

        List<String> winners = winnersMap.stream()
                .map(w -> "USER_" + w.get("account_id"))
                .toList();

        // Ledger entries
        for (Map<String, Object> winner : winnersMap) {
            int userId = (int) winner.get("account_id");
            double userBet = ((Number) winner.get("amount")).doubleValue();
            double userPayout = payoutPool * (userBet / winnersMap.stream()
                    .mapToDouble(b -> ((Number) b.get("amount")).doubleValue()).sum());

            repl.executeLine("INSERT INTO ledger_entries VALUES (" +
                    System.currentTimeMillis() + ", 999, -" + userPayout + ", 'ROUND_PAYOUT', 'USER_" + userId + "')");

            repl.executeLine("INSERT INTO ledger_entries VALUES (" +
                    System.currentTimeMillis() + ", " + userId + ", " + userPayout + ", 'ROUND_PAYOUT', 'ROUND_" + roundId + "')");
        }

        // Maintenance ledger
        repl.executeLine("INSERT INTO ledger_entries VALUES (" +
                System.currentTimeMillis() + ", 999, -" + maintenanceCut + ", 'MAINTENANCE_FEE', 'ROUND_" + roundId + "')");
        repl.executeLine("INSERT INTO ledger_entries VALUES (" +
                System.currentTimeMillis() + ", 998, " + maintenanceCut + ", 'MAINTENANCE_FEE', 'ROUND_" + roundId + "')");

//        repl.executeLine("UPDATE rounds SET status = 'CLOSED' WHERE id = " + roundId);

        return new CloseRoundResponse(roundId, winners, maintenanceCut);
    }

    // get all rounds
    @GetMapping
    public List<Map<String, Object>> getAllRounds() {
        return repl.query("SELECT * FROM rounds");
    }
}
