package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidOfferStatusException extends DomainException {
    public InvalidOfferStatusException() {
        super(OfferMessages.INVALID_OFFER_STATUS);
    }
}
