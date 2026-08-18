package com.letraaletra.api.features.game.domain.board.theme.repository;

import com.letraaletra.api.features.game.domain.board.theme.Theme;

import java.util.Optional;

public interface FindTheme {
    Optional<Theme> findById(String themeId);
}
