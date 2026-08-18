package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class CanNotAcceptTheRequestException extends DomainException {
    public CanNotAcceptTheRequestException() {
        super(FriendMessages.CAN_NOT_ACCEPT_THE_REQUEST);
    }
}
