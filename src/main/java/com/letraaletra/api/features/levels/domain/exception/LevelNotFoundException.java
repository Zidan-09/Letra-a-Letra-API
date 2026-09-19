package com.letraaletra.api.features.levels.domain.exception;

import com.letraaletra.api.features.levels.domain.LevelMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class LevelNotFoundException extends NotFoundDomainException {
    public LevelNotFoundException() {
        super(LevelMessages.LEVEL_NOT_FOUND);
    }
}
