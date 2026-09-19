package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidOfferExpirationException extends BadRequestDomainException {
    public InvalidOfferExpirationException() {
        super(OfferMessages.INVALID_OFFER_EXPIRATION);
    }
}
