package com.letraaletra.api.features.matchmaking.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum MatchmakingMessages implements MessageCode {
    USER_ALREADY_ON_QUEUE("user_already_on_queue");

    private final String message;

    MatchmakingMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
