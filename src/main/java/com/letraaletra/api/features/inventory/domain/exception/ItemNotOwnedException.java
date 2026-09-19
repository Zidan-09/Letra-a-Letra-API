package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class ItemNotOwnedException extends NotFoundDomainException {
    public ItemNotOwnedException() {
        super(InventoryMessages.ITEM_NOT_OWNED);
    }
}
