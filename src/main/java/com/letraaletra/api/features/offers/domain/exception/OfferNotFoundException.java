package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class OfferNotFoundException extends NotFoundDomainException {
    public OfferNotFoundException() {
        super(OfferMessages.OFFER_NOT_FOUND);
    }
}
