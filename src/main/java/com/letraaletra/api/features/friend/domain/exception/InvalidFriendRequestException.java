package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidFriendRequestException extends DomainException {
    public InvalidFriendRequestException() {
        super(FriendMessages.INVALID_FRIEND_REQUEST);
    }
}
