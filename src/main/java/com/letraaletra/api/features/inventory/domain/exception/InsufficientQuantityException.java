package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class InsufficientQuantityException extends ConflictDomainException {
    public InsufficientQuantityException() {
        super(InventoryMessages.INSUFFICIENT_QUANTITY);
    }
}
