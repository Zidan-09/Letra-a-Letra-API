package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidItemException extends BadRequestDomainException {
    public InvalidItemException() {
        super(ItemMessages.INVALID_ITEM);
    }
}
