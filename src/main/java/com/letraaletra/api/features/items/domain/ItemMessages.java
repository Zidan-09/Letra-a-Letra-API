package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum ItemMessages implements MessageCode {
    INVALID_ITEM("the item definition is invalid"),
    ITEM_ALREADY_EXISTS("an item with this name already exists"),
    ITEM_NOT_FOUND("the item was not found"),
    IMAGE_TOO_LARGE("the image exceeds the maximum allowed size of 5 MB"),
    INVALID_IMAGE_TYPE("the provided file is not a valid image"),
    IMAGE_CONVERSION_FAILED("failed to convert the image");

    private final String message;

    ItemMessages(String message) {
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
