package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.domain.board.theme.Theme;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;

import java.util.List;
import java.util.Random;

public class PickRandomThemeWordsService {
    private final ThemeRepository themeRepository;
    private final Random random = new Random();

    public PickRandomThemeWordsService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
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