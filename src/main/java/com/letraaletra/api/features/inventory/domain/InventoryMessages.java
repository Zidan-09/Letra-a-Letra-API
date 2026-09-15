package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum InventoryMessages implements MessageCode {
    ITEM_NOT_OWNED("the user does not own this item"),
    DUPLICATE_UNIQUE_ITEM("this unique item is already owned"),
    ITEM_NOT_AVAILABLE("this item is not available"),
    MAX_STACK_EXCEEDED("the maximum stack size would be exceeded"),
    INVALID_QUANTITY("the quantity is invalid"),
    NON_CONSUMABLE_ITEM("this item cannot be consumed"),
    NON_EQUIPABLE_ITEM("this item cannot be equipped"),
    INSUFFICIENT_QUANTITY("there is not enough quantity"),
    INAPPLICABLE_CONTEXT("this item cannot be used in this context");

    private final String message;

    InventoryMessages(String message) {
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
