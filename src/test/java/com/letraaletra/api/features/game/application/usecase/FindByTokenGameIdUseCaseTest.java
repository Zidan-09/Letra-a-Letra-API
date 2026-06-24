package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindByTokenInput;
import com.letraaletra.api.features.game.application.output.FindByTokenOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
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
class FindByTokenGameIdUseCaseTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private Actor actor;

    @Mock
    private Game game;

    @InjectMocks
    private FindByTokenGameIdUseCase useCase;

    private UUID gameId;

    @BeforeEach
    void setup() {
        gameId = UUID.randomUUID();
    }

    @Test
    void shouldReturnGameAndTokenWhenTokenIsValid() {
        FindByTokenInput input = new FindByTokenInput("token-123");

        when(tokenService.getTokenContent("token-123"))
                .thenReturn(gameId);

        when(actorManager.get(gameId.toString()))
                .thenReturn(actor);

        when(actor.getGame())
                .thenReturn(game);

        FindByTokenOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals("token-123", output.token());
        assertEquals(game, output.game());

        verify(tokenService).getTokenContent("token-123");
        verify(actorManager).get(gameId.toString());
        verify(actor).getGame();
    }

    @Test
    void shouldPropagateExceptionWhenTokenIsInvalid() {
        FindByTokenInput input = new FindByTokenInput("invalid-token");

        when(tokenService.getTokenContent("invalid-token"))
                .thenThrow(new InvalidTokenException());

        assertThrows(
                InvalidTokenException.class,
                () -> useCase.execute(input)
        );

        verify(actorManager, never()).get(anyString());
    }

    @Test
    void shouldPropagateExceptionWhenActorDoesNotExist() {
        FindByTokenInput input = new FindByTokenInput("token-123");

        when(tokenService.getTokenContent("token-123"))
                .thenReturn(gameId);

        when(actorManager.get(gameId.toString()))
                .thenThrow(new GameNotFoundException());

        assertThrows(
                GameNotFoundException.class,
                () -> useCase.execute(input)
        );
    }
}