package com.letraaletra.api.features.friend.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum FriendMessages implements MessageCode {
    FRIENDS_FOUND("friends_found"),
    REQUEST_ACCEPTED("request_accepted"),

    INVALID_FRIEND_REQUEST("invalid_friend_request"),
    CAN_NOT_ACCEPT_THE_REQUEST("can_not_accept_the_request"),
    CAN_NOT_DECLINE_THE_REQUEST("can_not_decline_the_request"),
    FRIEND_NOT_FOUND("friend_not_found"),
    FRIEND_REQUEST_STILL_PENDING("friend_request_still_pending");

    private final String message;

    FriendMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
