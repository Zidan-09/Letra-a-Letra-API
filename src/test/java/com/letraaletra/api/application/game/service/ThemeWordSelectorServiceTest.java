package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.domain.repository.ThemeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class ThemeWordSelectorServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private Random random;

    @InjectMocks
    private ThemeWordSelectorService selector;

    @Test
    @DisplayName("Should return a list with 5 words from a selected theme")
    void pickRandomWords() {
        List<String> wordsMock = List.of("t1", "t2", "t3", "t4", "t5", "t6");

        Theme theme = new Theme("id", "test", wordsMock);

        List<String> words = selector.pickRandomWords(theme);

        Assertions.assertEquals(5, words.size());
    }

    @Test
    @DisplayName("Should return a list with 5 words from a random theme")
    void pickRandomThemeWords() {
        Theme theme1 = new Theme("id1", "test1", List.of("t1", "t2", "t3", "t4", "t5", "t6"));
        Theme theme2 = new Theme("id2", "test2", List.of("t1", "t2", "t3", "t4", "t5", "t6"));

        Mockito.when(themeRepository.findAll())
                .thenReturn(List.of(theme1, theme2));

        Mockito.when(random.nextInt(2))
                .thenReturn(1);

        List<String> result = selector.pickRandomThemeWords();

        Assertions.assertEquals(5, result.size());
        Assertions.assertTrue(theme2.getWords().contains(result.getFirst()));
    }
}