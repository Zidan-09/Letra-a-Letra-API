package com.letraaletra.api.features.user.domain.exceptions;

import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.features.user.domain.UserMessages;

public class InvalidUserAvatarSelectedException extends DomainException {
    public InvalidUserAvatarSelectedException() {
        super(UserMessages.INVALID_AVATAR);
    }
}
