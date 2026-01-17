package com.stan.webapp.dto;

import java.util.List;

public class CloseRoundResponse {

    private int roundId;
    private List<String> winners;
    private double maintenanceCut;

    public CloseRoundResponse() {}

    public CloseRoundResponse(int roundId, List<String> winners, double maintenanceCut) {
        this.roundId = roundId;
        this.winners = winners;
        this.maintenanceCut = maintenanceCut;
    }

    // ✅ Getters & Setters
    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public List<String> getWinners() {
        return winners;
    }

    public void setWinners(List<String> winners) {
        this.winners = winners;
    }

    public double getMaintenanceCut() {
        return maintenanceCut;
    }

    public void setMaintenanceCut(double maintenanceCut) {
        this.maintenanceCut = maintenanceCut;
    }
}
