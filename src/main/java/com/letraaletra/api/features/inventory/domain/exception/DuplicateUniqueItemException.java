package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class DuplicateUniqueItemException extends DomainException {
    public DuplicateUniqueItemException() {
        super(InventoryMessages.DUPLICATE_UNIQUE_ITEM);
    }
}
