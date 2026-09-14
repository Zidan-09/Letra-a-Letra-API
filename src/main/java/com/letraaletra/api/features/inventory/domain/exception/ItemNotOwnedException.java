package com.letraaletra.api.features.inventory.domain.exception;

import com.letraaletra.api.features.inventory.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ItemNotOwnedException extends DomainException {
    public ItemNotOwnedException() {
        super(ItemMessages.ITEM_NOT_OWNED);
    }
}
