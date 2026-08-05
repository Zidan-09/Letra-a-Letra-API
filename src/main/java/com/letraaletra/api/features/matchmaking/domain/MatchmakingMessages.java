package com.letraaletra.api.features.matchmaking.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum MatchmakingMessages implements MessageCode {
    USER_IS_NOT_ON_QUEUE("the user is not in the queue"),
    USER_LEFT_QUEUE("the user has left the queue"),
    USER_ALREADY_ON_QUEUE("the user is already in the queue");

    private final String message;

    MatchmakingMessages(String message) {
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
