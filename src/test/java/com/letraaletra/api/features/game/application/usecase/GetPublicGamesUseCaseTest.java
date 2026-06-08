package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPublicGamesUseCaseTest {

    @Mock
    private GameQueryService gameQueryService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private GetPublicGamesUseCase useCase;

    @Test
    void shouldReturnEmptyListWhenNoPublicGamesExist() {
        when(gameQueryService.getPublic())
                .thenReturn(List.of());

        GetGamesOutput output = useCase.execute();

        assertNotNull(output);
        assertTrue(output.games().isEmpty());
        assertTrue(output.tokens().isEmpty());

        verify(tokenService, never()).generateToken(anyString());
    }

    @Test
    void shouldReturnGamesAndGeneratedTokens() {
        Game game1 = mock(Game.class);
        Game game2 = mock(Game.class);

        when(game1.getId()).thenReturn("game-1");
        when(game2.getId()).thenReturn("game-2");

        when(gameQueryService.getPublic())
                .thenReturn(List.of(game1, game2));

        when(tokenService.generateToken("game-1"))
                .thenReturn("token-1");

        when(tokenService.generateToken("game-2"))
                .thenReturn("token-2");

        GetGamesOutput output = useCase.execute();

        assertEquals(2, output.games().size());

        Map<String, String> tokens = output.tokens();

        assertEquals(2, tokens.size());
        assertEquals("token-1", tokens.get("game-1"));
        assertEquals("token-2", tokens.get("game-2"));

        assertTrue(output.games().contains(game1));
        assertTrue(output.games().contains(game2));
    }

    @Test
    void shouldGenerateTokenForEachPublicGame() {
        Game game1 = mock(Game.class);
        Game game2 = mock(Game.class);
        Game game3 = mock(Game.class);

        when(game1.getId()).thenReturn("game-1");
        when(game2.getId()).thenReturn("game-2");
        when(game3.getId()).thenReturn("game-3");

        when(gameQueryService.getPublic())
                .thenReturn(List.of(game1, game2, game3));

        when(tokenService.generateToken(anyString()))
                .thenReturn("token");

        useCase.execute();

        verify(tokenService).generateToken("game-1");
        verify(tokenService).generateToken("game-2");
        verify(tokenService).generateToken("game-3");

        verify(tokenService, times(3))
                .generateToken(anyString());
    }
}