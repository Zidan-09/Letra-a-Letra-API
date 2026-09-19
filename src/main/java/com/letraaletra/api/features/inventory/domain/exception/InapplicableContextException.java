package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InapplicableContextException extends BadRequestDomainException {
    public InapplicableContextException() {
        super(InventoryMessages.INAPPLICABLE_CONTEXT);
    }
}
