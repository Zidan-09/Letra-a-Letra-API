package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ItemNotFoundException extends DomainException {
    public ItemNotFoundException() {
        super(ItemMessages.ITEM_NOT_FOUND);
    }
}
