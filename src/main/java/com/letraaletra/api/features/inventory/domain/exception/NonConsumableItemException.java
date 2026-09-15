package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class NonConsumableItemException extends DomainException {
    public NonConsumableItemException() {
        super(InventoryMessages.NON_CONSUMABLE_ITEM);
    }
}
