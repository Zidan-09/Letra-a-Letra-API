package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindByCodeUseCaseTest {

    @Mock
    private GameQueryService gameQueryService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private FindByCodeUseCase useCase;

    private UUID gameId;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
    }

    @Test
    void shouldReturnTokenWhenGameExists() {
        FindByCodeInput input = new FindByCodeInput("ABC123");

        Game game = mock(Game.class);

        when(gameQueryService.findByCode("ABC123"))
                .thenReturn(game);

        when(game.getId())
                .thenReturn(gameId.toString());

        when(tokenService.generateToken(gameId))
                .thenReturn("token");

        FindByCodeOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals("token", output.token());

        verify(gameQueryService).findByCode("ABC123");
        verify(tokenService).generateToken(gameId);
    }

    @Test
    void shouldThrowExceptionWhenGameDoesNotExist() {
        FindByCodeInput input = new FindByCodeInput("ABC123");

        when(gameQueryService.findByCode("ABC123"))
                .thenReturn(null);

        assertThrows(
                GameNotFoundException.class,
                () -> useCase.execute(input)
        );

        verify(tokenService, never()).generateToken(any());
    }
}