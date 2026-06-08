package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.domain.board.theme.Theme;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;

import java.util.List;
import java.util.Random;

public class PickRandomThemeWordsService {
    private final ThemeRepository themeRepository;
    private final Random random;

    public PickRandomThemeWordsService(ThemeRepository themeRepository, Random random) {
        this.themeRepository = themeRepository;
        this.random = random;
    }

    public List<String> execute() {
        List<Theme> themes = themeRepository.findAll();
        validateThemes(themes);

        Theme randomTheme = themes.get(random.nextInt(themes.size()));

        return randomTheme.pickRandomWords(5, random);
    }

    private void validateThemes(List<Theme> themes) {
        if (themes.isEmpty()) {
            throw new IllegalStateException("No themes available");
        }
    }
}