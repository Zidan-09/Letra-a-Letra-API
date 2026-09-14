package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class DuplicateUniqueItemException extends DomainException {
    public DuplicateUniqueItemException() {
        super(ItemMessages.DUPLICATE_UNIQUE_ITEM);
    }
}
