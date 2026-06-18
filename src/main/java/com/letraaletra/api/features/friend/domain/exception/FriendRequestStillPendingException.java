package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class FriendRequestStillPendingException extends DomainException {
    public FriendRequestStillPendingException() {
        super(FriendMessages.FRIEND_REQUEST_STILL_PENDING);
    }
}
