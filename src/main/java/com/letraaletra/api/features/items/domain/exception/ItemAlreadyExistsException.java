package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.item.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ItemAlreadyExistsException extends DomainException {
    public ItemAlreadyExistsException() {
        super(ItemMessages.ITEM_ALREADY_EXISTS);
    }
}
