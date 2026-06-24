package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    private UUID gameId1;
    private UUID gameId2;

    @BeforeEach
    void setup() {
        gameId1 = UUID.randomUUID();
        gameId2 = UUID.randomUUID();
    }

    @Test
    void shouldReturnEmptyListWhenNoPublicGamesExist() {
        when(gameQueryService.getPublic())
                .thenReturn(List.of());

        GetGamesOutput output = useCase.execute();

        assertNotNull(output);
        assertTrue(output.games().isEmpty());
        assertTrue(output.tokens().isEmpty());

        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void shouldReturnGamesAndGeneratedTokens() {
        Game game1 = mock(Game.class);
        Game game2 = mock(Game.class);

        when(game1.getId()).thenReturn(gameId1.toString());
        when(game2.getId()).thenReturn(gameId2.toString());

        when(gameQueryService.getPublic())
                .thenReturn(List.of(game1, game2));

        when(tokenService.generateToken(gameId1))
                .thenReturn("token-1");

        when(tokenService.generateToken(gameId2))
                .thenReturn("token-2");

        GetGamesOutput output = useCase.execute();

        assertEquals(2, output.games().size());

        Map<String, String> tokens = output.tokens();

        assertEquals(2, tokens.size());
        assertEquals("token-1", tokens.get(gameId1.toString()));
        assertEquals("token-2", tokens.get(gameId2.toString()));

        assertTrue(output.games().contains(game1));
        assertTrue(output.games().contains(game2));
    }

    @Test
    void shouldGenerateTokenForEachPublicGame() {
        Game game1 = mock(Game.class);
        Game game2 = mock(Game.class);
        Game game3 = mock(Game.class);

        UUID gameId3 = UUID.randomUUID();

        when(game1.getId()).thenReturn(gameId1.toString());
        when(game2.getId()).thenReturn(gameId2.toString());
        when(game3.getId()).thenReturn(gameId3.toString());

        when(gameQueryService.getPublic())
                .thenReturn(List.of(game1, game2, game3));

        when(tokenService.generateToken(any()))
                .thenReturn("token");

        useCase.execute();

        verify(tokenService).generateToken(gameId1);
        verify(tokenService).generateToken(gameId2);
        verify(tokenService).generateToken(gameId3);

        verify(tokenService, times(3))
                .generateToken(any());
    }
}