package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class NonConsumableItemException extends DomainException {
    public NonConsumableItemException() {
        super(ItemMessages.NON_CONSUMABLE_ITEM);
    }
}
