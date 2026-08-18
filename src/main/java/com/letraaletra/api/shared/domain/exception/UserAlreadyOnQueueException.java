package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class UserAlreadyOnQueueException extends DomainException {
    public UserAlreadyOnQueueException() {
        super(MatchmakingMessages.USER_ALREADY_ON_QUEUE);
    }
}
