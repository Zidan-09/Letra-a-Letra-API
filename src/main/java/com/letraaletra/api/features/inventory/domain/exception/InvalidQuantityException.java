package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidQuantityException extends BadRequestDomainException {
    public InvalidQuantityException() {
        super(InventoryMessages.INVALID_QUANTITY);
    }
}
