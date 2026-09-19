package com.letraaletra.api.features.queue.domain.exception;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class UserAlreadyOnQueueException extends ConflictDomainException {
    public UserAlreadyOnQueueException() {
        super(MatchmakingMessages.USER_ALREADY_ON_QUEUE);
    }
}
