package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidConsumableItemDurationException extends DomainException {
    public InvalidConsumableItemDurationException() {
        super(ItemMessages.INVALID_CONSUMABLE_ITEM_DURATION);
    }
}
