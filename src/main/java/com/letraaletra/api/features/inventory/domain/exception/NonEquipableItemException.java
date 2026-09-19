package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class NonEquipableItemException extends BadRequestDomainException {
    public NonEquipableItemException() {
        super(InventoryMessages.NON_EQUIPABLE_ITEM);
    }
}
