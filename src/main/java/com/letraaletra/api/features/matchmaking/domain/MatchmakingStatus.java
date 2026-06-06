package com.letraaletra.api.features.matchmaking.domain;

public enum MatchmakingStatus {
    SEARCHING("searching"),
    FOUNDED("founded");

    private final String response;

    MatchmakingStatus(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
