package com.letraaletra.api.features.admin.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum AdminMessages implements MessageCode {
    ADMIN_NOT_FOUND("the administrator was not found"),
    EMAIL_ALREADY_IN_USE("the email address is already in use"),
    ALREADY_HAVE_THIS_PERMISSION("the administrator already has this permission"),
    INVALID_ADMIN_OPERATION("the requested administrator operation is invalid"),
    PERMISSION_DENIED("permission was denied"),
    INVALID_PERMISSION("the provided permission is invalid");

    private final String message;

    AdminMessages(String message) {
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
