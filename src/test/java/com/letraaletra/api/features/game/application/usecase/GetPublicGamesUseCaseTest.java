package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.GetPublicGamesInput;
import com.letraaletra.api.features.game.application.output.GetPublicGamesOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    void shouldReturnEmptyPageWhenNoPublicGamesExist() {
        GetPublicGamesInput input = new GetPublicGamesInput(0, 10, Sort.unsorted());

        Page<Game> page = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 10),
                0
        );

        when(gameQueryService.getPublic(input))
                .thenReturn(page);

        GetPublicGamesOutput output = useCase.execute(input);

        assertNotNull(output);
        assertTrue(output.games().isEmpty());
        assertEquals(0, output.games().getTotalElements());

        verify(gameQueryService).getPublic(input);
    }

    @Test
    void shouldReturnPublicGames() {
        GetPublicGamesInput input = new GetPublicGamesInput(0, 10, Sort.unsorted());

        Game game1 = mock(Game.class);
        Game game2 = mock(Game.class);

        Page<Game> page = new PageImpl<>(
                List.of(game1, game2),
                PageRequest.of(0, 10),
                2
        );

        when(gameQueryService.getPublic(input))
                .thenReturn(page);

        GetPublicGamesOutput output = useCase.execute(input);

        assertEquals(2, output.games().getContent().size());
        assertTrue(output.games().getContent().contains(game1));
        assertTrue(output.games().getContent().contains(game2));
        assertEquals(2, output.games().getTotalElements());

        verify(gameQueryService).getPublic(input);
    }
}