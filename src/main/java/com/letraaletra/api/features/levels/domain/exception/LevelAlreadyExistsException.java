package com.letraaletra.api.features.levels.domain.exception;

import com.letraaletra.api.features.levels.domain.LevelMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class LevelAlreadyExistsException extends DomainException {
    public LevelAlreadyExistsException() {
        super(LevelMessages.LEVEL_ALREADY_EXISTS);
    }
}
