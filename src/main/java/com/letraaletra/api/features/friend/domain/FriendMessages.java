package com.letraaletra.api.features.friend.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum FriendMessages implements MessageCode {
    FRIENDS_FOUND("friends_found"),
    REQUEST_ACCEPTED("request_accepted"),

    INVALID_FRIEND_REQUEST("invalid_friend_request"),
    FRIEND_REQUEST_STILL_PENDING("friend_request_still_pending");

    private final String message;

    FriendMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "";
    }
}
