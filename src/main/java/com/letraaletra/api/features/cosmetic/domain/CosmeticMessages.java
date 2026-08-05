package com.letraaletra.api.features.cosmetic.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum CosmeticMessages implements MessageCode {
    FAILED_TO_LOAD_COSMETICS("failed to load cosmetics"),
    COSMETIC_NOT_FOUND("the cosmetic was not found"),
    INVALID_COSMETIC("the selected cosmetic is invalid");

    private final String message;

    CosmeticMessages(String message) {
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
