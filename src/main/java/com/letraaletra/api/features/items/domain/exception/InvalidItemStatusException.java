package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidItemStatusException extends DomainException {
    public InvalidItemStatusException() {
        super(ItemMessages.INVALID_ITEM_STATUS);
    }
}
