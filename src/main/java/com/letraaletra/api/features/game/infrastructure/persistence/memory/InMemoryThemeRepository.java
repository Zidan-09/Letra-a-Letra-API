package com.letraaletra.api.features.game.infrastructure.persistence.memory;

import com.letraaletra.api.features.game.domain.repository.ThemeRepository;
import com.letraaletra.api.features.game.domain.board.theme.Theme;
import com.letraaletra.api.features.game.infrastructure.loader.ThemeLoader;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryThemeRepository implements ThemeRepository {

    private final Map<String, Theme> themeMap;

    public InMemoryThemeRepository(ThemeLoader loader) {
        this.themeMap = new ConcurrentHashMap<>(Map.copyOf(loader.load()));
    }

    @Override
    public void save(Theme theme) {
        themeMap.put(theme.getThemeId(), theme);
    }

    @Override
    public Optional<Theme> findById(String themeId) {
        if (!themeMap.containsKey(themeId)) return Optional.empty();

        Theme theme = themeMap.get(themeId);

        return Optional.of(theme);
    }

    @Override
    public List<Theme> get() {
        return List.copyOf(themeMap.values());
    }

    @Override
    public List<String> getIds() {
        return List.copyOf(themeMap.keySet());
    }
}
