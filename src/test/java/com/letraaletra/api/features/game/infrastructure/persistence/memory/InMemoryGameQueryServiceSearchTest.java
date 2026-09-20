package com.letraaletra.api.features.game.infrastructure.persistence.memory;

import com.letraaletra.api.features.game.application.input.FindActiveGameByRoomNameInput;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryGameQueryServiceSearchTest {

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private Actor actorCasa;
    @Mock
    private Actor actorCasamento;
    @Mock
    private Actor actorMinhaCasa;
    @Mock
    private Actor actorOutro;

    private InMemoryGameQueryService queryService;
    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        queryService = new InMemoryGameQueryService(actorManager);
        principal = new AuthenticatedUser(UUID.randomUUID(), "Jogador Teste", false, false);

        when(actorCasa.getGame()).thenReturn(game("C001", "Casa"));
        when(actorCasamento.getGame()).thenReturn(game("C002", "Casamento"));
        when(actorMinhaCasa.getGame()).thenReturn(game("C003", "Minha Casa"));
        when(actorOutro.getGame()).thenReturn(game("C004", "Outro"));

        when(actorManager.getAllActors()).thenReturn(List.of(actorCasa, actorCasamento, actorMinhaCasa, actorOutro));
    }

    @Test
    void shouldFindByPartialNameIgnoringCase() {
        FindActiveGameByRoomNameInput input =
                new FindActiveGameByRoomNameInput(principal, "casa", 0, 10, Sort.unsorted());

        Page<Game> page = queryService.searchActiveByRoomName(input);

        assertEquals(3, page.getTotalElements());
        assertTrue(page.getContent().stream().map(Game::getRoomName).toList()
                .containsAll(List.of("Casa", "Casamento", "Minha Casa")));
    }

    @Test
    void shouldOrderPrefixMatchesFirstThenAlphabetically() {
        FindActiveGameByRoomNameInput input =
                new FindActiveGameByRoomNameInput(principal, "casa", 0, 10, Sort.unsorted());

        Page<Game> page = queryService.searchActiveByRoomName(input);

        List<String> names = page.getContent().stream().map(Game::getRoomName).toList();

        assertEquals(List.of("Casa", "Casamento", "Minha Casa"), names);
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {
        FindActiveGameByRoomNameInput input =
                new FindActiveGameByRoomNameInput(principal, "zzz", 0, 10, Sort.unsorted());

        Page<Game> page = queryService.searchActiveByRoomName(input);

        assertTrue(page.isEmpty());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void shouldPaginateResults() {
        FindActiveGameByRoomNameInput first =
                new FindActiveGameByRoomNameInput(principal, "casa", 0, 2, Sort.unsorted());
        FindActiveGameByRoomNameInput second =
                new FindActiveGameByRoomNameInput(principal, "casa", 1, 2, Sort.unsorted());

        assertEquals(2, queryService.searchActiveByRoomName(first).getContent().size());
        assertEquals(1, queryService.searchActiveByRoomName(second).getContent().size());
        assertEquals(3, queryService.searchActiveByRoomName(first).getTotalElements());
    }

    private static Game game(String code, String roomName) {
        return Game.create(code, roomName, new RoomSettings(false, false), GameType.CUSTOM);
    }
}
