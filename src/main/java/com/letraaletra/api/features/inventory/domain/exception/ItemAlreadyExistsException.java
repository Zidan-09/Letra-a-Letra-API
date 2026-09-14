package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ItemAlreadyExistsException extends DomainException {
    public ItemAlreadyExistsException() {
        super(ItemMessages.ITEM_ALREADY_EXISTS);
    }
}
