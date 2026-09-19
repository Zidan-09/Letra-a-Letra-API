package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class CanNotAcceptTheRequestException extends ConflictDomainException {
    public CanNotAcceptTheRequestException() {
        super(FriendMessages.CAN_NOT_ACCEPT_THE_REQUEST);
    }
}
