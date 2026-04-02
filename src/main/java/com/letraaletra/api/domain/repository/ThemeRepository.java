package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.game.board.theme.Theme;

import java.util.List;

public interface ThemeRepository {
    void save(Theme theme);
    Theme findById(String themeId);
    List<Theme> findAll();
}
