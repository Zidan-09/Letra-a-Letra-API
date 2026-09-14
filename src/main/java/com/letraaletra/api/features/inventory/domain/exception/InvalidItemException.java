package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidItemException extends DomainException {
    public InvalidItemException() {
        super(ItemMessages.INVALID_ITEM);
    }
}
