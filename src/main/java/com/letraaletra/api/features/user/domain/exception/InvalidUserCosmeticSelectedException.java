package com.letraaletra.api.features.user.domain.exception;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class InvalidUserCosmeticSelectedException extends DomainException {
    public InvalidUserCosmeticSelectedException() {
        super(UserMessages.INVALID_COSMETIC);
    }
}
