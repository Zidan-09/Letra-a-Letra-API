package com.letraaletra.api.features.cosmetic.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum CosmeticMessages implements MessageCode {
    FAILED_TO_LOAD_COSMETICS("failed to load cosmetics"),
    COSMETIC_NOT_FOUND("the cosmetic was not found"),
    COSMETIC_ALREADY_EXISTS("A cosmetic with this name already exists"),
    INVALID_COSMETIC("the selected cosmetic is invalid"),

    IMAGE_TOO_LARGE("the image exceeds the maximum allowed size of 5 MB"),
    INVALID_IMAGE_TYPE("the provided file is not a valid image"),
    IMAGE_CONVERSION_FAILED("failed to convert the image");

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
