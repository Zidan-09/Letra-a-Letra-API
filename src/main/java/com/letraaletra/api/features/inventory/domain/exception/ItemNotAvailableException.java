package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class ItemNotAvailableException extends BadRequestDomainException {
    public ItemNotAvailableException() {
        super(InventoryMessages.ITEM_NOT_AVAILABLE);
    }
}
