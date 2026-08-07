package com.letraaletra.api.features.cosmetic.domain.exceptions;

import com.letraaletra.api.features.cosmetic.domain.CosmeticMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidImageTypeException extends DomainException {
    public InvalidImageTypeException() {
        super(CosmeticMessages.INVALID_IMAGE_TYPE);
    }
}
