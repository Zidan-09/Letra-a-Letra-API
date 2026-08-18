package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class OfferAlreadyPurchasedException extends DomainException {
    public OfferAlreadyPurchasedException() {
        super(OfferMessages.OFFER_ALREADY_PURCHASED);
    }
}
