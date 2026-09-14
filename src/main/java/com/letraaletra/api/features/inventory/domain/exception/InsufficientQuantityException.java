package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InsufficientQuantityException extends DomainException {
    public InsufficientQuantityException() {
        super(ItemMessages.INSUFFICIENT_QUANTITY);
    }
}
