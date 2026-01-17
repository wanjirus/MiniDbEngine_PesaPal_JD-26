package com.stan.webapp.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PlaceBetRequest {

    @NotNull
    private Integer accountId;

    @NotNull
    private Integer roundId;

    @NotNull
    @Min(0)
    private Double amount;

    public PlaceBetRequest() {}
    public PlaceBetRequest(Integer accountId, Integer roundId, Double amount) {
        this.accountId = accountId;
        this.roundId = roundId;
        this.amount = amount;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getRoundId() {
        return roundId;
    }

    public void setRoundId(Integer roundId) {
        this.roundId = roundId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
