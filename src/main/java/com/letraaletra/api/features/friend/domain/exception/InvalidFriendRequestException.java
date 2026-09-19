package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidFriendRequestException extends BadRequestDomainException {
    public InvalidFriendRequestException() {
        super(FriendMessages.INVALID_FRIEND_REQUEST);
    }
}
