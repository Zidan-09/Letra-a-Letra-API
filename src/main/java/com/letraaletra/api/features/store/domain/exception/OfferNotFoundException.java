package com.letraaletra.api.features.store.domain.exception;

import com.letraaletra.api.features.store.domain.StoreOfferMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class OfferNotFoundException extends DomainException {
    public OfferNotFoundException() {
        super(StoreOfferMessages.OFFER_NOT_FOUND);
    }
}
