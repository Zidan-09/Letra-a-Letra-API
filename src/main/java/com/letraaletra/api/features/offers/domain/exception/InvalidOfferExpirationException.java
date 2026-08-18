package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidOfferExpirationException extends DomainException {
    public InvalidOfferExpirationException() {
        super(OfferMessages.INVALID_OFFER_EXPIRATION);
    }
}
