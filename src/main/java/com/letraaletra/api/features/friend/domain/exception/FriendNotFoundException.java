package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class FriendNotFoundException extends DomainException {
    public FriendNotFoundException() {
        super(FriendMessages.FRIEND_NOT_FOUND);
    }
}
