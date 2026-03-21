package com.letraaletra.api.service;

import com.letraaletra.api.domain.theme.Theme;
import com.letraaletra.api.infra.repository.ThemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ThemeWordSelectorTest {

    private final Random random = new Random();

    @Mock
    private ThemeRepository themeRepository;

    @Autowired
    @InjectMocks
    private ThemeWordSelector themeWordSelector;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should get a random String array with 5 words of an selected theme")
    void pickRandomWords() {

    }

    @Test
    @DisplayName("Should get a random String array with 5 words of an random theme")
    void pickRandomThemeWords() {

    }
}