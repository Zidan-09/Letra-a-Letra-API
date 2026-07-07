package com.letraaletra.api.features.admin.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum AdminMessages implements MessageCode {
    ADMIN_NOT_FOUND("admin_not_found"),
    EMAIL_ALREADY_IN_USE("email_already_in_use"),
    ALREADY_HAVE_THIS_PERMISSION("already_have_this_permission"),
    INVALID_PERMISSION("invalid_permission");

    private final String message;

    AdminMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
