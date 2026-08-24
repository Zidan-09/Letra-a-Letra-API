package com.letraaletra.api.features.ranking.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum RankingMessages implements MessageCode {
    USER_LEFT_QUEUE("the user has left the queue");

    private final String message;

    RankingMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
