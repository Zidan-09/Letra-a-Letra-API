package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class MaxStackExceededException extends ConflictDomainException {
    public MaxStackExceededException() {
        super(InventoryMessages.MAX_STACK_EXCEEDED);
    }
}
