package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.InventoryMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InapplicableContextException extends DomainException {
    public InapplicableContextException() {
        super(InventoryMessages.INAPPLICABLE_CONTEXT);
    }
}
