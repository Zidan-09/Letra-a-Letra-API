package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class DuplicateUniqueItemException extends ConflictDomainException {
    public DuplicateUniqueItemException() {
        super(InventoryMessages.DUPLICATE_UNIQUE_ITEM);
    }
}
