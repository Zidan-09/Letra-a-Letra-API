package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class OfferAlreadyPurchasedException extends ConflictDomainException {
    public OfferAlreadyPurchasedException() {
        super(OfferMessages.OFFER_ALREADY_PURCHASED);
    }
}
