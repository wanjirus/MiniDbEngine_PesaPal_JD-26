package com.stan.webapp.dto;

public class UserSummaryDTO {

    public int id;
    public String name;
    public int age;
    public double balance;
    public int betsPlaced;
    public int wins;
    public double totalWinAmount;

    public UserSummaryDTO(
            int id,
            String name,
            int age,
            double balance,
            int betsPlaced,
            int wins,
            double totalWinAmount
    ) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.balance = balance;
        this.betsPlaced = betsPlaced;
        this.wins = wins;
        this.totalWinAmount = totalWinAmount;
    }
}
