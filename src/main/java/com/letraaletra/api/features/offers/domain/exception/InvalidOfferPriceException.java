package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidOfferPriceException extends DomainException {
    public InvalidOfferPriceException() {
        super(OfferMessages.INVALID_OFFER_PRICE);
    }
}
