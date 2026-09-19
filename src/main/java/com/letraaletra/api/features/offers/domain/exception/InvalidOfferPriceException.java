package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidOfferPriceException extends BadRequestDomainException {
    public InvalidOfferPriceException() {
        super(OfferMessages.INVALID_OFFER_PRICE);
    }
}
