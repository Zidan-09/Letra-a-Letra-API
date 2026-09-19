package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidItemStatusException extends BadRequestDomainException {
    public InvalidItemStatusException() {
        super(ItemMessages.INVALID_ITEM_STATUS);
    }
}
