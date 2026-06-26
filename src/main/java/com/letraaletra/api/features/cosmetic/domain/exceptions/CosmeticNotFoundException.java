package com.letraaletra.api.features.cosmetic.domain.exceptions;

import com.letraaletra.api.features.cosmetic.domain.CosmeticMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class CosmeticNotFoundException extends DomainException {
    public CosmeticNotFoundException() {
        super(CosmeticMessages.COSMETIC_NOT_FOUND);
    }
}
