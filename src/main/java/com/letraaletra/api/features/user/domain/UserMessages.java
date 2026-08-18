package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum UserMessages implements MessageCode {
    USER_NOT_FOUND("the user was not found"),

    SESSION_NOT_FOUND("the session was not found"),

    USER_ALREADY_EXISTS("the user already exists"),
    EMAIL_ALREADY_IN_USE("the email address is already in use"),
    NICKNAME_ALREADY_IN_USE("the nickname is already in use"),
    USER_CANNOT_CHANGE_NICKNAME("the user cannot change their nickname at this time"),

    INVALID_COSMETIC("the selected cosmetic is invalid"),
    INSUFFICIENT_BALANCE("the user does not have enough balance"),

    INVALID_CREDENTIALS("the provided credentials are invalid"),
    USER_DISABLED("the user account is disabled"),
    USER_BLOCKED("the user account is blocked"),

    USER_ALREADY_IN_GAME("the user is already in a game"),
    USER_NOT_IN_GAME("the user is not currently in a game"),

    INVALID_USER_DATA("the provided user data is invalid"),
    INVALID_TOKEN("the provided token is invalid"),

    MAX_ATTEMPTS_EXCEEDED("the maximum number of attempts has been exceeded"),
    SAME_PASSWORD("the new password must be different from the current password"),

    USER_BANNED_FROM_GAME("the user was banned from game"),
    USER_ALREADY_BANNED("the user is already banned"),
    USER_DOES_NOT_HAVE_BAN("the user does not have an active ban"),

    INVALID_ROOM_POSITION("the provided room position is invalid");

    private final String message;

    UserMessages(String message) {
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
