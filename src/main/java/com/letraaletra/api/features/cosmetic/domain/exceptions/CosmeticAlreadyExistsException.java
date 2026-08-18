package com.letraaletra.api.features.cosmetic.domain.exceptions;

import com.letraaletra.api.features.cosmetic.domain.CosmeticMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class CosmeticAlreadyExistsException extends DomainException {
    public CosmeticAlreadyExistsException() {
        super(CosmeticMessages.COSMETIC_ALREADY_EXISTS);
    }
}
