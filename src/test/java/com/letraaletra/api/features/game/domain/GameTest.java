package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.exception.GameIsRunningException;
import com.letraaletra.api.features.game.domain.exception.RoomFullException;
import com.letraaletra.api.features.game.domain.exception.UserBannedException;
import com.letraaletra.api.features.game.domain.exception.UserNotInGameException;
import com.letraaletra.api.features.game.domain.factory.GameStateFactory;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.participant.domain.exception.InvalidRoomPositionException;
import com.letraaletra.api.features.participant.domain.exception.ParticipantAlreadyBannedException;
import com.letraaletra.api.features.participant.domain.exception.ParticipantNotBannedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameTest {

    private Game game;
    private Participant host;

    @Mock private Board mockBoard;
    @Mock private GameStateFactory mockStateGenerator;
    @Mock private GameState mockGameState;

    @BeforeEach
    void setUp() {
        host = new Participant("host-id", "sess-1", "DonoDaSala", List.of());
        RoomSettings defaultSettings = new RoomSettings(true, false);

        game = new Game("game-1", "CODE12", "Lobby dos Devs", defaultSettings, host, GameType.CUSTOM);
    }

    @Nested
    @DisplayName("Testes de Inicialização e Criação do Jogo")
    class InitializationTests {

        @Test
        @DisplayName("Deve inicializar a sala com o Host na posição de Player e status WAITING")
        void shouldInitializeGameWithHostAsPlayer() {
            assertEquals(GameStatus.WAITING, game.getGameStatus());
            assertEquals("host-id", game.getHostId());
            assertEquals("host-id", game.getCreatedById());
            assertEquals(ParticipantRole.PLAYER, host.getRole());
            assertEquals("host-id", game.getPositions().get(0));
            assertEquals(1, game.getAmountPlayers());
        }
    }

    @Nested
    @DisplayName("Testes do Fluxo de Entrada (Join)")
    class JoinFlowTests {

        @Test
        @DisplayName("Deve permitir a entrada do segundo participante como PLAYER")
        void shouldAllowSecondParticipantAsPlayer() {
            Participant p2 = new Participant("p2-id", "sess-2", "JogadorDois", List.of());

            game.join(p2);

            assertEquals(ParticipantRole.PLAYER, p2.getRole());
            assertEquals(2, game.getAmountPlayers());
            assertEquals("p2-id", game.getPositions().get(1), "Deve ocupar a próxima posição livre (1)");
        }

        @Test
        @DisplayName("Deve entrar como SPECTATOR caso a sala já possua 2 jogadores ativos")
        void shouldJoinAsSpectatorWhenRoomHasTwoPlayers() {
            Participant p2 = new Participant("p2-id", "sess-2", "JogadorDois", List.of());
            Participant p3 = new Participant("p3-id", "sess-3", "EspectadorUm", List.of());

            game.join(p2);
            game.join(p3);

            assertEquals(ParticipantRole.SPECTATOR, p3.getRole());
            assertEquals(2, game.getAmountPlayers(), "Total de Players ativos deve continuar sendo 2");
            assertEquals("p3-id", game.getPositions().get(2), "Deve ir para a vaga de espectador");
        }

        @Test
        @DisplayName("Deve lançar UserBannedException se o usuário estiver na Blacklist")
        void shouldThrowExceptionWhenUserIsBlacklisted() {
            Participant p2 = new Participant("banned-id", "sess-2", "ToxicPlayer", List.of());
            game.addToBlackList("banned-id");

            assertThrows(UserBannedException.class, () -> game.join(p2));
        }

        @Test
        @DisplayName("Deve lançar RoomFullException ao tentar entrar se a sala não permitir espectadores e já tiver 2 players")
        void shouldThrowExceptionWhenRoomDoesNotAllowSpectatorsAndIsFull() {
            RoomSettings strictSettings = new RoomSettings(false, false);
            Game strictGame = new Game("g2", "C", "N", strictSettings, host, GameType.CUSTOM);

            strictGame.join(new Participant("p2", "s2", "J2", List.of()));

            Participant p3 = new Participant("p3", "s3", "J3", List.of());
            assertThrows(RoomFullException.class, () -> strictGame.join(p3));
        }
    }

    @Nested
    @DisplayName("Testes de Remoção de Usuário e Passagem de Host")
    class RemoveFlowTests {

        @Test
        @DisplayName("Deve passar a liderança da sala para o próximo se o Host sair")
        void shouldPassHostToNextParticipantWhenHostLeaves() {
            Participant p2 = new Participant("p2-id", "sess-2", "Gamer2", List.of());
            game.join(p2);
            assertEquals("host-id", game.getHostId());

            game.remove("host-id");

            assertEquals("p2-id", game.getHostId(), "O Host deve ser transferido para o p2");
            assertFalse(game.getPositions().containsValue("host-id"), "A posição antiga do host deve ser limpa");
        }

        @Test
        @DisplayName("Deve lançar UserNotInGameException ao tentar remover alguém que não está no mapa")
        void shouldThrowExceptionWhenRemovingGhostUser() {
            assertThrows(UserNotInGameException.class, () -> game.remove("id-inexistente"));
        }
    }

    @Nested
    @DisplayName("Testes de Troca de Posição (Change Position)")
    class ChangePositionTests {

        @Test
        @DisplayName("Deve permitir trocar de posição alterando o papel de Player para Espectador")
        void shouldSwitchFromPlayerToSpectatorCorrectly() {
            assertEquals(ParticipantRole.PLAYER, host.getRole());

            game.changePosition("host-id", 5);

            assertEquals(ParticipantRole.SPECTATOR, host.getRole(), "Acima da posição 2 deve virar espectador");
            assertNull(game.getPositions().get(0), "A antiga posição 0 deve ficar vaga");
            assertEquals("host-id", game.getPositions().get(5));
        }

        @Test
        @DisplayName("Deve lançar GameIsRunningException ao tentar mudar de lugar com o jogo rolando")
        void shouldPreventPositionChangeWhenGameIsRunning() {
            game.setGameStatus(GameStatus.RUNNING);

            assertThrows(GameIsRunningException.class, () -> game.changePosition("host-id", 3));
        }

        @Test
        @DisplayName("Deve lançar InvalidRoomPositionException se a vaga de destino já estiver ocupada")
        void shouldThrowExceptionWhenTargetPositionIsOccupied() {
            Participant p2 = new Participant("p2-id", "sess-2", "Gamer2", List.of());
            game.join(p2); // Ocupa a posição 1 por padrão

            assertThrows(InvalidRoomPositionException.class, () -> game.changePosition("host-id", 1));
        }
    }

    @Nested
    @DisplayName("Testes de Moderação e Blacklist")
    class BlacklistTests {

        @Test
        @DisplayName("Deve gerenciar adições e remoções na blacklist corretamente")
        void shouldManageBlacklistStates() {
            String targetId = "spammer-123";
            assertFalse(game.isBlackListed(targetId));

            game.addToBlackList(targetId);
            assertTrue(game.isBlackListed(targetId));

            assertThrows(ParticipantAlreadyBannedException.class, () -> game.addToBlackList(targetId));

            game.removeFromBlackList(targetId);
            assertFalse(game.isBlackListed(targetId));

            assertThrows(ParticipantNotBannedException.class, () -> game.removeFromBlackList(targetId));
        }
    }

    @Nested
    @DisplayName("Testes de Transição de Estado (Start Game)")
    class GameLifecycleTests {

        @Test
        @DisplayName("Deve mudar o status da sala para RUNNING e popular o gameState")
        void shouldTransitionToRunningOnStart() {
            when(mockStateGenerator.generate(any(), eq(mockBoard))).thenReturn(mockGameState);

            game.start(mockBoard, mockStateGenerator);

            assertEquals(GameStatus.RUNNING, game.getGameStatus());
            assertEquals(mockGameState, game.getGameState());
        }
    }
}