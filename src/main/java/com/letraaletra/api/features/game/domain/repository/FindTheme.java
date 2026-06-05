package com.letraaletra.api.features.game.domain.repository;

import com.letraaletra.api.features.game.domain.board.theme.Theme;

import java.util.List;

public interface FindTheme {
    Theme findById(String themeId);
    List<Theme> findAll();
}
