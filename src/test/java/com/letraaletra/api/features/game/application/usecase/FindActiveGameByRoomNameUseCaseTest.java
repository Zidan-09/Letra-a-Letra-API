package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.FindActiveGameByRoomNameInput;
import com.letraaletra.api.features.game.application.output.FindActiveGameByRoomNameOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindActiveGameByRoomNameUseCaseTest {

    @Mock
    private GameQueryService gameQueryService;

    @InjectMocks
    private FindActiveGameByRoomNameUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "Jogador Teste", false, false);
    }

    @Test
    void shouldDelegateToQueryServiceAndReturnPage() {
        FindActiveGameByRoomNameInput input =
                new FindActiveGameByRoomNameInput(principal, "sala", 0, 10, Sort.unsorted());

        Game game = mock(Game.class);
        Page<Game> page = new PageImpl<>(List.of(game), PageRequest.of(0, 10), 1);

        when(gameQueryService.searchActiveByRoomName(input)).thenReturn(page);

        FindActiveGameByRoomNameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(1, output.games().getTotalElements());
        assertTrue(output.games().getContent().contains(game));
        verify(gameQueryService).searchActiveByRoomName(input);
    }

    @Test
    void shouldReturnEmptyPageWhenNoRoomMatches() {
        FindActiveGameByRoomNameInput input =
                new FindActiveGameByRoomNameInput(principal, "inexistente", 0, 10, Sort.unsorted());

        Page<Game> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(gameQueryService.searchActiveByRoomName(input)).thenReturn(page);

        FindActiveGameByRoomNameOutput output = useCase.execute(input);

        assertNotNull(output);
        assertTrue(output.games().isEmpty());
        verify(gameQueryService).searchActiveByRoomName(input);
    }
}
