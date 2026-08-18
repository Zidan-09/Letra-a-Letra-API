package com.letraaletra.api.shared.domain.exception;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class UserIsNotOnQueueException extends DomainException {
    public UserIsNotOnQueueException() {
        super(MatchmakingMessages.USER_IS_NOT_ON_QUEUE);
    }
}
