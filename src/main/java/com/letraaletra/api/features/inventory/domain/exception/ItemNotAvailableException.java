package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ItemNotAvailableException extends DomainException {
    public ItemNotAvailableException() {
        super(ItemMessages.ITEM_NOT_AVAILABLE);
    }
}
