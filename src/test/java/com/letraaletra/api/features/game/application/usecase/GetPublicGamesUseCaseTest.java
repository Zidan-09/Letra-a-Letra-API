package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPublicGamesUseCaseTest {

    @Mock
    private GameQueryService gameQueryService;

    @InjectMocks
    private GetPublicGamesUseCase useCase;

    @Test
    void shouldReturnEmptyListWhenNoPublicGamesExist() {
        when(gameQueryService.getPublic())
                .thenReturn(List.of());

        GetGamesOutput output = useCase.execute(null);

        assertNotNull(output);
        assertTrue(output.games().isEmpty());
    }

    @Test
    void shouldReturnGames() {
        Game game1 = mock(Game.class);
        Game game2 = mock(Game.class);

        when(gameQueryService.getPublic())
                .thenReturn(List.of(game1, game2));

        GetGamesOutput output = useCase.execute(null);

        assertEquals(2, output.games().size());

        assertTrue(output.games().contains(game1));
        assertTrue(output.games().contains(game2));
    }
}