package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class FriendNotFoundException extends NotFoundDomainException {
    public FriendNotFoundException() {
        super(FriendMessages.FRIEND_NOT_FOUND);
    }
}
