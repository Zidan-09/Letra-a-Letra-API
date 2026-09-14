package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidQuantityException extends DomainException {
    public InvalidQuantityException() {
        super(ItemMessages.INVALID_QUANTITY);
    }
}
