package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class FriendRequestStillPendingException extends ConflictDomainException {
    public FriendRequestStillPendingException() {
        super(FriendMessages.FRIEND_REQUEST_STILL_PENDING);
    }
}
