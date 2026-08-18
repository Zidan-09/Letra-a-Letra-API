package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidPaymentException extends DomainException {
    public InvalidPaymentException() {
        super(OfferMessages.INVALID_PAYMENT_TYPE);
    }
}
