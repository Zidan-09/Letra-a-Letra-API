package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class OfferNotFoundException extends DomainException {
    public OfferNotFoundException() {
        super(OfferMessages.OFFER_NOT_FOUND);
    }
}
