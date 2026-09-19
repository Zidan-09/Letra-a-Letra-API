package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class ItemNotFoundException extends NotFoundDomainException {
    public ItemNotFoundException() {
        super(ItemMessages.ITEM_NOT_FOUND);
    }
}
