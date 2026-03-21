package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.infra.loader.ThemeLoader;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryThemeRepository implements ThemeRepository {

    private final ThemeLoader loader;

    public InMemoryThemeRepository(ThemeLoader loader) {
        this.loader = loader;
    }

    @Override
    public List<Theme> findAll() {
        return loader.load();
    }
}
