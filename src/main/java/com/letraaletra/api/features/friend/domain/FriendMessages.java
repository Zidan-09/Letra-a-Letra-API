package com.letraaletra.api.features.friend.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum FriendMessages implements MessageCode {
    FRIENDS_FOUND("friends were found"),
    REQUEST_ACCEPTED("the friend request has been accepted"),

    INVALID_FRIEND_REQUEST("the friend request is invalid"),
    CAN_NOT_ACCEPT_THE_REQUEST("the friend request cannot be accepted"),
    CAN_NOT_DECLINE_THE_REQUEST("the friend request cannot be declined"),
    FRIEND_NOT_FOUND("the friend was not found"),
    FRIEND_REQUEST_STILL_PENDING("the friend request is still pending");

    private final String message;

    FriendMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
