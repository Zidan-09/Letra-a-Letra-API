package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.features.game.domain.exception.ThemeNotFoundException;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SelectWordsThemeService implements SelectThemeService {
    private final ThemeRepository themeRepository;
    private final Random random = new Random();

    @Override
    public List<String> select() {
        List<String> keys = themeRepository.getIds();

        String key = keys.get(random.nextInt(keys.size()));

        return select(key);
    }

    @Override
    public List<String> select(String themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(ThemeNotFoundException::new)
                .pickRandomWords(5);
    }
}
