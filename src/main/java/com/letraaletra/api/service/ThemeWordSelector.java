package com.letraaletra.api.service;

import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.infra.repository.ThemeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class ThemeWordSelector {
    private final Random random = new Random();
    private final ThemeRepository themeRepository;

    public ThemeWordSelector(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<String> pickRandomWords(Theme theme, int amount) {
        List<String> shuffled = new ArrayList<>(theme.getWords());
        Collections.shuffle(shuffled, random);

        return shuffled.stream()
                .limit(amount)
                .toList();
    }

    public List<String> pickRandomThemeWords(int amount) {
        List<Theme> themes = themeRepository.findAll();

        Theme randomTheme = themes.get(random.nextInt(themes.size()));

        return pickRandomWords(randomTheme, amount);
    }
}
