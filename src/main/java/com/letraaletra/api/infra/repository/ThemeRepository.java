package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.theme.Theme;

import java.util.List;

public interface ThemeRepository {
    List<Theme> findAll();
}
