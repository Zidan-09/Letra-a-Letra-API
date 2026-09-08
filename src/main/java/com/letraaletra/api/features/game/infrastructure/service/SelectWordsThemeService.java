package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.features.game.domain.board.theme.exception.ThemeNotFoundException;
import com.letraaletra.api.features.game.domain.board.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SelectWordsThemeService implements SelectThemeService {
    private final ThemeRepository themeRepository;

    @Override
    public List<String> select() {
        List<String> keys = themeRepository.getIds();

        if (keys == null || keys.isEmpty()) {
            throw new ThemeNotFoundException();
        }

        String key = keys.get(ThreadLocalRandom.current().nextInt(keys.size()));

        return select(key);
    }

    @Override
    public List<String> select(String themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(ThemeNotFoundException::new)
                .pickRandomWords(5);
    }
}
