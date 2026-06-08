package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.domain.board.theme.Theme;
import com.letraaletra.api.features.game.domain.repository.ThemeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PickRandomThemeWordsServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private Random random;

    @InjectMocks
    private PickRandomThemeWordsService service;

    @Test
    void shouldPickWordsFromRandomTheme() {
        Theme theme1 = mock(Theme.class);
        Theme theme2 = mock(Theme.class);

        List<Theme> themes = List.of(theme1, theme2);

        when(themeRepository.findAll()).thenReturn(themes);

        when(random.nextInt(2)).thenReturn(1);

        List<String> expectedWords = List.of(
                "word1",
                "word2",
                "word3",
                "word4",
                "word5"
        );

        when(theme2.pickRandomWords(eq(5), any(Random.class)))
                .thenReturn(expectedWords);

        List<String> result = service.execute();

        assertNotNull(result);
        assertEquals(expectedWords, result);

        verify(themeRepository).findAll();
        verify(random).nextInt(2);
        verify(theme2).pickRandomWords(eq(5), any(Random.class));
        verify(theme1, never()).pickRandomWords(anyInt(), any());
    }

    @Test
    void shouldHandleSingleTheme() {
        Theme theme = mock(Theme.class);

        when(themeRepository.findAll()).thenReturn(List.of(theme));
        when(random.nextInt(1)).thenReturn(0);

        List<String> expectedWords = List.of("a", "b", "c", "d", "e");

        when(theme.pickRandomWords(eq(5), any(Random.class)))
                .thenReturn(expectedWords);

        List<String> result = service.execute();

        assertEquals(expectedWords, result);
        verify(themeRepository).findAll();
        verify(random).nextInt(1);
        verify(theme).pickRandomWords(eq(5), any(Random.class));
    }

    @Test
    void shouldThrowExceptionWhenNoThemesExist() {
        when(themeRepository.findAll()).thenReturn(List.of());

        assertThrows(
                IllegalStateException.class,
                () -> service.execute()
        );

        verify(themeRepository).findAll();
        verifyNoInteractions(random);
    }
}