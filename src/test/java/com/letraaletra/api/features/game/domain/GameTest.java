package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.factory.GameStateFactory;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameTest {

    private Game game;
    private Participant host;
    private UUID hostId;
    private UUID gameId;

    @Mock
    private Board mockBoard;

    @Mock
    private GameStateFactory mockStateGenerator;

    @Mock
    private GameState mockGameState;

    @BeforeEach
    void setUp() {
        hostId = UUID.randomUUID();
        gameId = UUID.randomUUID();

        host = new Participant(
                hostId,
                "sess-1",
                "DonoDaSala",
                List.of()
        );

        RoomSettings settings = new RoomSettings(true, false);

        game = new Game(
                gameId,
                "CODE12",
                "Lobby dos Devs",
                settings,
                host,
                GameType.CUSTOM
        );
    }

    @Test
    @DisplayName("Deve criar uma instância através da factory")
    void shouldCreateGameUsingFactory() {

        Game game = Game.create(
                gameId,
                "CODE12",
                "Lobby",
                new RoomSettings(true, false),
                host,
                GameType.CUSTOM
        );

        assertNotNull(game);
        assertEquals(gameId, game.getId());
        assertEquals("CODE12", game.getCode());
    }

    @Nested
    @DisplayName("Inicialização")
    class InitializationTests {

        @Test
        @DisplayName("Deve inicializar corretamente")
        void shouldInitializeGameCorrectly() {

            assertEquals(gameId, game.getId());
            assertEquals("CODE12", game.getCode());
            assertEquals("Lobby dos Devs", game.getRoomName());

            assertEquals(GameStatus.WAITING, game.getGameStatus());

            assertEquals(hostId, game.getHostId());
            assertEquals(hostId, game.getCreatedById());

            assertEquals(GameType.CUSTOM, game.getGameType());

            assertEquals(ParticipantRole.PLAYER, host.getRole());

            assertNull(game.getGameState());

            assertNotNull(game.getParticipants());
        }
    }

    @Nested
    @DisplayName("Start da partida")
    class StartTests {

        @Test
        @DisplayName("Deve iniciar a partida")
        void shouldStartGame() {

            when(mockStateGenerator.generate(any(), eq(mockBoard)))
                    .thenReturn(mockGameState);

            game.start(mockBoard, mockStateGenerator);

            assertEquals(GameStatus.RUNNING, game.getGameStatus());
            assertSame(mockGameState, game.getGameState());
            verify(mockStateGenerator)
                    .generate(game.getParticipants().getParticipants(), mockBoard);
        }
    }

    @Test
    @DisplayName("Deve remover o jogador do GameState durante a partida")
    void shouldRemovePlayerFromGameState() {

        game.updateGameState(mockGameState);
        game.setGameStatus(GameStatus.RUNNING);

        game.remove(hostId);

        verify(mockGameState).removePlayer(hostId);
    }

    @Test
    @DisplayName("Não deve lançar exceção ao remover o último participante")
    void shouldAllowRemovingLastParticipant() {

        assertDoesNotThrow(() -> game.remove(hostId));
    }

    @Test
    @DisplayName("Deve transferir o host quando o host atual sair")
    void shouldTransferHost() {

        Participant p2 = new Participant(
                UUID.randomUUID(),
                "s2",
                "Jogador",
                List.of()
        );

        game.getParticipants().join(p2, game.getRoomSettings());

        game.remove(hostId);

        assertEquals(p2.getUserId(), game.getHostId());
    }

    @Nested
    @DisplayName("Atualização de estado")
    class UpdateStateTests {

        @Test
        @DisplayName("Deve atualizar o estado do jogo")
        void shouldUpdateGameState() {

            game.updateGameState(mockGameState);

            assertSame(mockGameState, game.getGameState());
        }

        @Test
        @DisplayName("Deve atualizar o status do jogo")
        void shouldUpdateGameStatus() {

            game.setGameStatus(GameStatus.CLOSED);

            assertEquals(GameStatus.CLOSED, game.getGameStatus());
        }
    }
}