package com.letraaletra.api.features.levels.domain.exception;

import com.letraaletra.api.features.levels.domain.LevelMessages;
import com.letraaletra.api.shared.domain.exception.ConflictDomainException;

public class LevelAlreadyExistsException extends ConflictDomainException {
    public LevelAlreadyExistsException() {
        super(LevelMessages.LEVEL_ALREADY_EXISTS);
    }
}
