package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.domain.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class ThemeWordSelectorService {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private Random random;

    private static final int WORDS_PER_GAME = 5;

    public List<String> pickRandomWords(Theme theme) {
        List<String> shuffled = new ArrayList<>(theme.getWords());
        Collections.shuffle(shuffled, random);

        return shuffled.stream()
                .limit(WORDS_PER_GAME)
                .toList();
    }

    public List<String> pickRandomThemeWords() {
        List<Theme> themes = themeRepository.findAll();

        Theme randomTheme = themes.get(random.nextInt(themes.size()));

        return pickRandomWords(randomTheme);
    }
}
