package com.letraaletra.api.features.game.domain.exception;

import com.letraaletra.api.features.game.domain.GameMessages;
import com.letraaletra.api.shared.domain.DomainException;

public class ThemeNotFoundException extends DomainException {
    public ThemeNotFoundException() {
        super(GameMessages.THEME_NOT_FOUND);
    }
}
