package com.letraaletra.api.features.user.domain.session;

public enum SessionRevocationReason {
    LOGOUT,
    NEW_LOGIN,
    PASSWORD_CHANGED,
    PASSWORD_RESET,
    USER_BANNED,
    TOKEN_REUSE,
    OTHER
}
