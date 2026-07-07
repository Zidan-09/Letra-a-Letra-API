package com.letraaletra.api.features.offers.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum OfferMessages implements MessageCode {
    INVALID_OFFER_STATUS("invalid_offer_status"),
    OFFER_NOT_FOUND("offer_not_found");

    private final String message;

    OfferMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
