package com.letraaletra.api.features.offers.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum OfferMessages implements MessageCode {
    INVALID_PAYMENT_TYPE("the selected payment type is invalid"),
    OFFER_ALREADY_PURCHASED("the offer has already been purchased"),
    INVALID_OFFER_EXPIRATION("the offer expiration is invalid"),
    INVALID_OFFER_STATUS("the offer status is invalid"),
    OFFER_NOT_FOUND("the offer was not found");

    private final String message;

    OfferMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    public String getMessage() {
        return message;
    }
}
