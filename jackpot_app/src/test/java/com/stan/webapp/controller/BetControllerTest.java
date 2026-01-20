package com.stan.webapp.controller;

import com.stan.pesapal.repl.Repl;
import com.stan.webapp.dto.PlaceBetRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetControllerTest {

    @Mock
    private Repl repl;

    @InjectMocks
    private BetController betController;

    @Test
    void placeBet_successful() {

        PlaceBetRequest request = new PlaceBetRequest();
        request.setAccountId(1);
        request.setRoundId(10);
        request.setAmount(50.0);

        // Mock ledger balance = 100
        when(repl.query(anyString())).thenReturn(
                List.of(
                        Map.of("amount", 70.0),
                        Map.of("amount", 30.0)
                )
        );

        // call the method
        String result = betController.placeBet(request);

        // Assert test
        Assertions.assertEquals("✅ Bet placed for account 1 in round 10", result);

        verify(repl).query(
                "SELECT * FROM ledger_entries WHERE account_id = 1"
        );

        // verify inserts: debit, credit, bet record
        verify(repl, times(3)).executeLine(anyString());
    }
}
