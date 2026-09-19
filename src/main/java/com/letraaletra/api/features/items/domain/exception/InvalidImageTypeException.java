package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.exception.BadRequestDomainException;

public class InvalidImageTypeException extends BadRequestDomainException {
    public InvalidImageTypeException() {
        super(ItemMessages.INVALID_IMAGE_TYPE);
    }
}
