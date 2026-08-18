package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
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
class FindByCodeUseCaseTest {

    @Mock
    private GameQueryService gameQueryService;

    @InjectMocks
    private FindByCodeUseCase useCase;

    private UUID gameId;
    private String roomCode;
    private FindByCodeInput input;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
        roomCode = "ABC123";
        input = new FindByCodeInput(roomCode);
    }

    @Test
    @DisplayName("Deve retornar o ID do jogo com sucesso quando a sala for encontrada pelo código")
    void shouldReturnGameIdWhenGameExists() {
        // Arrange
        Game game = mock(Game.class);
        when(game.getId()).thenReturn(gameId);
        when(gameQueryService.findByCode(roomCode)).thenReturn(Optional.of(game));

        // Act
        FindByCodeOutput output = useCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(gameId, output.gameId());

        verify(gameQueryService).findByCode(roomCode);
        verify(game).getId();
    }

    @Test
    @DisplayName("Deve lançar GameNotFoundException quando não existir jogo associado ao código")
    void shouldThrowExceptionWhenGameDoesNotExist() {
        // Arrange
        when(gameQueryService.findByCode(roomCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                GameNotFoundException.class,
                () -> useCase.execute(input)
        );

        verify(gameQueryService).findByCode(roomCode);
    }
}