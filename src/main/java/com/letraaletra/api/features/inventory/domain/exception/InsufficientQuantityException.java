package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InsufficientQuantityException extends DomainException {
    public InsufficientQuantityException() {
        super(InventoryMessages.INSUFFICIENT_QUANTITY);
    }
}
