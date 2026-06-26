package com.letraaletra.api.features.store.domain;

import com.letraaletra.api.shared.domain.MessageCode;

public enum StoreOfferMessages implements MessageCode {
    INVALID_OFFER_STATUS("invalid_offer_status"),
    OFFER_NOT_FOUND("offer_not_found");

    private final String message;

    StoreOfferMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
