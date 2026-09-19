package com.letraaletra.api.features.queue.domain.exception;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class UserIsNotOnQueueException extends ConflictDomainException {
    public UserIsNotOnQueueException() {
        super(MatchmakingMessages.USER_IS_NOT_ON_QUEUE);
    }
}
