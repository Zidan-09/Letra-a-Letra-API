package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class MaxStackExceededException extends DomainException {
    public MaxStackExceededException() {
        super(ItemMessages.MAX_STACK_EXCEEDED);
    }
}
