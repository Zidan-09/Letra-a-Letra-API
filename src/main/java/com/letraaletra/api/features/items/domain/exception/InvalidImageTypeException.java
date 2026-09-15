package com.letraaletra.api.features.items.domain.exception;

import com.letraaletra.api.features.items.domain.ItemMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidImageTypeException extends DomainException {
    public InvalidImageTypeException() {
        super(ItemMessages.INVALID_IMAGE_TYPE);
    }
}
