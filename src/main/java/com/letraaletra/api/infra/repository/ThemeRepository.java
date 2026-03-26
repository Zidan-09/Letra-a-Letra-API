package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.theme.Theme;

import java.util.List;

public interface ThemeRepository {
    void save(Theme theme);
    Theme findById(String themeId);
    List<Theme> findAll();
}
