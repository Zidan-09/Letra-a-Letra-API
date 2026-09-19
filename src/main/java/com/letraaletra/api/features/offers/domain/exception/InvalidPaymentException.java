package com.letraaletra.api.features.offers.domain.exception;

import com.letraaletra.api.features.offers.domain.OfferMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidPaymentException extends BadRequestDomainException {
    public InvalidPaymentException() {
        super(OfferMessages.INVALID_PAYMENT_TYPE);
    }
}
