package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class NonEquipableItemException extends DomainException {
    public NonEquipableItemException() {
        super(InventoryMessages.NON_EQUIPABLE_ITEM);
    }
}
