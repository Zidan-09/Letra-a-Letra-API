package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class NonConsumableItemException extends BadRequestDomainException {
    public NonConsumableItemException() {
        super(InventoryMessages.NON_CONSUMABLE_ITEM);
    }
}
