package com.letraaletra.api.features.cosmetic.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum CosmeticMessages implements MessageCode {
    FAILED_TO_LOAD_COSMETICS("failed_to_load_cosmetics"),
    COSMETIC_NOT_FOUND("cosmetic_not_found");

    private final String message;

    CosmeticMessages(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
