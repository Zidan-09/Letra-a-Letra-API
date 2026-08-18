package com.letraaletra.api.features.levels.domain.exception;

import com.letraaletra.api.features.levels.domain.LevelMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class LevelNotFoundException extends DomainException {
    public LevelNotFoundException() {
        super(LevelMessages.LEVEL_NOT_FOUND);
    }
}
