package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.item.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidItemException extends DomainException {
    public InvalidItemException() {
        super(ItemMessages.INVALID_ITEM);
    }
}
