package com.letraaletra.api.features.cosmetic.domain.exceptions;

import com.letraaletra.api.features.cosmetic.domain.CosmeticMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class InvalidCosmeticException extends DomainException {
    public InvalidCosmeticException() {
        super(CosmeticMessages.INVALID_COSMETIC);
    }
}
