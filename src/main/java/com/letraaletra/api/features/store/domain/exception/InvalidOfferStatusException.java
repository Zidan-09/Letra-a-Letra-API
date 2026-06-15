package com.letraaletra.api.features.store.domain.exception;

import com.letraaletra.api.features.store.domain.StoreOfferMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidOfferStatusException extends DomainException {
    public InvalidOfferStatusException() {
        super(StoreOfferMessages.INVALID_OFFER_STATUS);
    }
}
