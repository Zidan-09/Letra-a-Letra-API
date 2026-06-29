package com.letraaletra.api.features.friend.domain.exception;

import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class CanNotDeclineTheRequestException extends DomainException {
    public CanNotDeclineTheRequestException() {
        super(FriendMessages.CAN_NOT_DECLINE_THE_REQUEST);
    }
}
