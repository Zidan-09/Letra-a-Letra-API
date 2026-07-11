package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.levels.application.input.FindLevelInput;
import com.letraaletra.api.features.levels.application.output.FindLevelOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.exception.LevelNotFoundException;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindLevelUseCaseTest {

    @Mock
    private LevelRepository levelRepository;

    @InjectMocks
    private FindLevelUseCase useCase;

    private UUID levelId;
    private FindLevelInput input;
    private Level mockLevel;

    @BeforeEach
    void setUp() {
        levelId = UUID.randomUUID();
        input = new FindLevelInput(levelId);
        mockLevel = mock(Level.class);
    }

    @Test
    @DisplayName("Should successfully return FindLevelOutput when the level exists in the repository")
    void shouldReturnLevelOutputWhenLevelExists() {
        when(levelRepository.find(levelId)).thenReturn(Optional.of(mockLevel));

        FindLevelOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockLevel, output.level()); // Assumindo método getter ou record component .level() baseado na assinatura
        verify(levelRepository, times(1)).find(levelId);
    }

    @Test
    @DisplayName("Should throw LevelNotFoundException when the level does not exist in the repository")
    void shouldThrowLevelNotFoundExceptionWhenLevelDoesNotExist() {
        when(levelRepository.find(levelId)).thenReturn(Optional.empty());

        assertThrows(LevelNotFoundException.class, () -> useCase.execute(input));

        verify(levelRepository, times(1)).find(levelId);
    }

    @Test
    @DisplayName("Should handle edge cases or throw exception when input record contains a null levelId mapping reference")
    void shouldThrowExceptionWhenLevelIdIsNull() {
        FindLevelInput invalidInput = new FindLevelInput(null);

        // Caso o repositório permita nulo e retorne vazio, ou lance erro de argumento inválido diretamente
        when(levelRepository.find(null)).thenThrow(new IllegalArgumentException("Identifier cannot be null"));

        assertThrows(RuntimeException.class, () -> useCase.execute(invalidInput));
    }

    @Test
    @DisplayName("Should throw NullPointerException or RuntimeException when the input context structure itself is null")
    void shouldThrowExceptionWhenInputContextIsNull() {
        assertThrows(RuntimeException.class, () -> useCase.execute(null));

        verifyNoInteractions(levelRepository);
    }
}