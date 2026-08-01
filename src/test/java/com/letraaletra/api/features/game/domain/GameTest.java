package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.exception.GameIsRunningException;
import com.letraaletra.api.features.game.domain.exception.InsufficientPlayersException;
import com.letraaletra.api.features.game.domain.factory.GameStateFactory;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameTest {

    private Game game;
    private RoomSettings roomSettings;

    @Mock
    private Board mockBoard;

    @Mock
    private GameState mockGameState;

    @Mock
    private User mockUser;

    @Mock
    private User mockSecondUser;

    @Mock
    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        roomSettings = new RoomSettings(true, false);
        game = Game.create(
                "CODE12",
                "Lobby dos Devs",
                roomSettings,
                GameType.CUSTOM
        );

        lenient().when(mockInventory.getItems()).thenReturn(Collections.emptyList());
        lenient().when(mockUser.getInventory()).thenReturn(mockInventory);
        lenient().when(mockSecondUser.getInventory()).thenReturn(mockInventory);
    }

    @Test
    @DisplayName("Deve criar uma instância válida através da Factory static")
    void shouldCreateGameUsingFactory() {
        assertNotNull(game);
        assertNotNull(game.getId());
        assertEquals("CODE12", game.getCode());
        assertEquals("Lobby dos Devs", game.getRoomName());
        assertEquals(GameStatus.WAITING, game.getGameStatus());
        assertEquals(GameType.CUSTOM, game.getGameType());
        assertEquals(roomSettings, game.getRoomSettings());
        assertNull(game.getCreatedById());
        assertNull(game.getHostId());
        assertNull(game.getGameState());
    }

    @Nested
    @DisplayName("Entrada de Jogadores (Join)")
    class JoinTests {

        @Test
        @DisplayName("Deve definir o primeiro jogador como criador, host e com role PLAYER")
        void shouldSetFirstPlayerAsHostAndCreator() {
            UUID userId = UUID.randomUUID();
            when(mockUser.getId()).thenReturn(userId);

            game.join(mockUser, "session-1");

            assertEquals(userId, game.getCreatedById());
            assertEquals(userId, game.getHostId());
            assertFalse(game.getParticipants().getParticipants().isEmpty());

            var hostParticipant = game.getParticipants().getParticipants().getFirst();
            assertEquals(ParticipantRole.PLAYER, hostParticipant.getRole());
        }

        @Test
        @DisplayName("Não deve alterar o host quando um segundo jogador entrar")
        void shouldNotChangeHostWhenSecondPlayerJoins() {
            UUID hostUserId = UUID.randomUUID();
            UUID secondUserId = UUID.randomUUID();

            when(mockUser.getId()).thenReturn(hostUserId);
            when(mockSecondUser.getId()).thenReturn(secondUserId);

            game.join(mockUser, "session-1");
            game.join(mockSecondUser, "session-2");

            assertEquals(hostUserId, game.getHostId());
            assertEquals(hostUserId, game.getCreatedById());
            assertEquals(2, game.getParticipants().getParticipants().size());
        }
    }

    @Nested
    @DisplayName("Início da Partida (Start)")
    class StartTests {

        @Test
        @DisplayName("Deve iniciar a partida e alterar o status para RUNNING quando houver participantes suficientes")
        void shouldStartGame() {
            when(mockUser.getId()).thenReturn(UUID.randomUUID());
            when(mockSecondUser.getId()).thenReturn(UUID.randomUUID());

            game.join(mockUser, "session-1");
            game.join(mockSecondUser, "session-2");

            game.getParticipants().getParticipants().get(1).changeRole(ParticipantRole.PLAYER);

            try (MockedStatic<GameStateFactory> mockedFactory = mockStatic(GameStateFactory.class)) {
                mockedFactory.when(() -> GameStateFactory.generate(any(), eq(mockBoard)))
                        .thenReturn(mockGameState);

                game.start(mockBoard);

                assertEquals(GameStatus.RUNNING, game.getGameStatus());
                assertSame(mockGameState, game.getGameState());
            }
        }

        @Test
        @DisplayName("Deve lançar InsufficientPlayersException ao tentar iniciar com menos de 2 jogadores")
        void shouldThrowExceptionWhenLessThanTwoPlayers() {
            when(mockUser.getId()).thenReturn(UUID.randomUUID());
            game.join(mockUser, "session-1");

            assertThrows(
                    InsufficientPlayersException.class,
                    () -> game.start(mockBoard)
            );
        }

        @Test
        @DisplayName("Deve lançar GameIsRunningException ao tentar iniciar jogo já em andamento")
        void shouldThrowExceptionWhenGameAlreadyRunning() {
            game.setGameStatus(GameStatus.RUNNING);

            assertThrows(
                    GameIsRunningException.class,
                    () -> game.start(mockBoard)
            );
        }
    }

    @Nested
    @DisplayName("Troca de Posição")
    class ChangePositionTests {

        @Test
        @DisplayName("Deve permitir alterar posição quando o jogo estiver em WAITING")
        void shouldAllowChangePositionWhenWaiting() {
            UUID userId = UUID.randomUUID();
            when(mockUser.getId()).thenReturn(userId);
            game.join(mockUser, "session-1");

            assertDoesNotThrow(() -> game.changePosition(userId, 1));
        }

        @Test
        @DisplayName("Deve lançar GameIsRunningException ao tentar alterar posição durante o jogo")
        void shouldThrowExceptionWhenChangingPositionWhileRunning() {
            UUID userId = UUID.randomUUID();
            when(mockUser.getId()).thenReturn(userId);
            game.join(mockUser, "session-1");

            game.setGameStatus(GameStatus.RUNNING);

            assertThrows(
                    GameIsRunningException.class,
                    () -> game.changePosition(userId, 1)
            );
        }
    }

    @Nested
    @DisplayName("Remoção de Jogadores")
    class RemoveTests {

        @Test
        @DisplayName("Deve remover jogador do GameState se a partida estiver em andamento")
        void shouldRemovePlayerFromGameStateWhenGameIsRunning() {
            UUID userId = UUID.randomUUID();
            when(mockUser.getId()).thenReturn(userId);
            game.join(mockUser, "session-1");

            game.updateGameState(mockGameState);
            game.setGameStatus(GameStatus.RUNNING);

            game.remove(userId);

            verify(mockGameState).removePlayer(userId);
        }

        @Test
        @DisplayName("Deve transferir a liderança (host) para o próximo participante quando o host sair")
        void shouldTransferHostWhenCurrentHostLeaves() {
            UUID hostUserId = UUID.randomUUID();
            UUID secondUserId = UUID.randomUUID();

            when(mockUser.getId()).thenReturn(hostUserId);
            when(mockSecondUser.getId()).thenReturn(secondUserId);

            game.join(mockUser, "session-1");
            game.join(mockSecondUser, "session-2");

            game.remove(hostUserId);

            assertEquals(secondUserId, game.getHostId());
        }

        @Test
        @DisplayName("Não deve lançar exceção nem quebrar ao remover o último participante")
        void shouldAllowRemovingLastParticipant() {
            UUID userId = UUID.randomUUID();
            when(mockUser.getId()).thenReturn(userId);
            game.join(mockUser, "session-1");

            assertDoesNotThrow(() -> game.remove(userId));
            assertTrue(game.getParticipants().getParticipants().isEmpty());
        }
    }

    @Nested
    @DisplayName("Atualizações de Estado e Status")
    class UpdateStateTests {

        @Test
        @DisplayName("Deve atualizar o estado da partida")
        void shouldUpdateGameState() {
            game.updateGameState(mockGameState);
            assertSame(mockGameState, game.getGameState());
        }

        @Test
        @DisplayName("Deve atualizar o status da partida")
        void shouldUpdateGameStatus() {
            game.setGameStatus(GameStatus.CLOSED);
            assertEquals(GameStatus.CLOSED, game.getGameStatus());
        }
    }
}