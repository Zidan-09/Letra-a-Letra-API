package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class CanNotDeclineTheRequestException extends ConflictDomainException {
    public CanNotDeclineTheRequestException() {
        super(FriendMessages.CAN_NOT_DECLINE_THE_REQUEST);
    }
}
