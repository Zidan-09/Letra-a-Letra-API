package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class ItemAlreadyExistsException extends ConflictDomainException {
    public ItemAlreadyExistsException() {
        super(ItemMessages.ITEM_ALREADY_EXISTS);
    }
}
