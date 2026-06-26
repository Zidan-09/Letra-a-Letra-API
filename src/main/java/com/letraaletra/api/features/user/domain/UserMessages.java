package com.letraaletra.api.features.user.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum UserMessages implements MessageCode {
    USER_CREATED("user_created"),
    USER_UPDATED("user_updated"),
    USER_DELETED("user_deleted"),
    USER_BANNED("user_banned"),

    USER_LOGGED("user_logged"),

    USER_FOUND("user_found"),
    USERS_FOUND("users_found"),
    USER_NOT_FOUND("user_not_found"),

    SESSION_NOT_FOUND("session_not_found"),

    USER_ALREADY_EXISTS("user_already_exists"),
    EMAIL_ALREADY_IN_USE("email_already_in_use"),
    NICKNAME_SETTER("nickname_setter"),
    NICKNAME_ALREADY_IN_USE("nickname_already_in_use"),
    USER_CANNOT_CHANGE_NICKNAME("user_cannot_change_nickname"),

    COSMETIC_EQUIPPED("cosmetic_equipped"),
    COSMETIC_UNLOCKED("cosmetic_unlocked"),
    INVALID_COSMETIC("invalid_cosmetic"),
    INSUFFICIENT_BALANCE("insufficient_balance"),

    INVALID_CREDENTIALS("invalid_credentials"),
    USER_DISABLED("user_disabled"),
    USER_BLOCKED("user_blocked"),

    USER_ALREADY_IN_GAME("user_already_in_game"),
    USER_NOT_IN_GAME("user_not_in_game"),

    INVALID_USER_DATA("invalid_user_data"),
    INVALID_TOKEN("invalid_token"),

    INVALID_ROOM_POSITION("invalid_room_position");

    private final String message;

    UserMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
