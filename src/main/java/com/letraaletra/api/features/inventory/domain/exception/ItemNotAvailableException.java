package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ItemNotAvailableException extends DomainException {
    public ItemNotAvailableException() {
        super(InventoryMessages.ITEM_NOT_AVAILABLE);
    }
}
