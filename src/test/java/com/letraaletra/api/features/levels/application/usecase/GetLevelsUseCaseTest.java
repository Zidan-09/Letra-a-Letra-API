package com.letraaletra.api.features.levels.application.usecase;

import com.letraaletra.api.features.levels.application.input.GetLevelsInput;
import com.letraaletra.api.features.levels.application.output.GetLevelsOutput;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLevelsUseCaseTest {

    @Mock
    private LevelRepository levelRepository;

    @InjectMocks
    private GetLevelsUseCase useCase;

    private GetLevelsInput mockInput;
    private Level mockLevel1;
    private Level mockLevel2;

    @BeforeEach
    void setUp() {
        mockInput = mock(GetLevelsInput.class);
        mockLevel1 = mock(Level.class);
        mockLevel2 = mock(Level.class);
    }

    @Test
    @DisplayName("Should successfully return GetLevelsOutput containing a populated list of levels when repository finds records")
    void shouldReturnPopulatedLevelsOutputSuccessfully() {
        List<Level> expectedLevels = List.of(mockLevel1, mockLevel2);
        when(levelRepository.get(mockInput)).thenReturn(expectedLevels);

        GetLevelsOutput output = useCase.execute(mockInput);

        assertNotNull(output);
        assertEquals(expectedLevels, output.levels()); // Assumindo record component ou getter .levels()
        assertEquals(2, output.levels().size());
        verify(levelRepository, times(1)).get(mockInput);
    }

    @Test
    @DisplayName("Should successfully return GetLevelsOutput containing an empty list when no levels match the input criteria")
    void shouldReturnEmptyLevelsOutputSuccessfully() {
        when(levelRepository.get(mockInput)).thenReturn(Collections.emptyList());

        GetLevelsOutput output = useCase.execute(mockInput);

        assertNotNull(output);
        assertTrue(output.levels().isEmpty());
        verify(levelRepository, times(1)).get(mockInput);
    }

    @Test
    @DisplayName("Should propagate any runtime exception thrown by the repository layer execution context")
    void shouldPropagateRepositoryExceptions() {
        when(levelRepository.get(mockInput)).thenThrow(new RuntimeException("Database timeout or connection lost"));

        assertThrows(RuntimeException.class, () -> useCase.execute(mockInput));
    }

    @Test
    @DisplayName("Should test resilience or throw exception when the input filter context structure itself is null")
    void shouldHandleOrThrowExceptionWhenInputContextIsNull() {
        // Caso o repositório lance erro ao receber nulo ou trate internamente
        when(levelRepository.get(null)).thenThrow(new IllegalArgumentException("Query criteria input filter cannot be null"));

        assertThrows(RuntimeException.class, () -> useCase.execute(null));
    }
}