package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidQuantityException extends DomainException {
    public InvalidQuantityException() {
        super(InventoryMessages.INVALID_QUANTITY);
    }
}
