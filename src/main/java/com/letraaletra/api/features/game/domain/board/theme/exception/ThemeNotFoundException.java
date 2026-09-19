package com.letraaletra.api.features.game.domain.board.theme.exception;

import com.letraaletra.api.features.game.domain.GameMessages;
import com.letraaletra.api.shared.domain.exception.NotFoundDomainException;

public class ThemeNotFoundException extends NotFoundDomainException {
    public ThemeNotFoundException() {
        super(GameMessages.THEME_NOT_FOUND);
    }
}
