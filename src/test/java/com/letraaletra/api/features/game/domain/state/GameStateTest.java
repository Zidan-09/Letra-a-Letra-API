package com.letraaletra.api.features.game.domain.state;

import com.letraaletra.api.features.game.domain.GameOverReasons;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.domain.exception.PlayerNotInGameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameStateTest {

    private GameState gameState;
    private Instant initialTurnEnds;

    @Mock private Board mockBoard;
    @Mock private Player mockPlayer1;
    @Mock private Player mockPlayer2;

    private UUID userId1;
    private UUID userId2;

    private UUID matchId;

    @BeforeEach
    void setUp() {
        initialTurnEnds = Instant.now().plusSeconds(45);

        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();

        when(mockPlayer1.getUserId()).thenReturn(userId1);
        when(mockPlayer2.getUserId()).thenReturn(userId2);

        Map<UUID, Player> playersMap = new LinkedHashMap<>();
        playersMap.put(userId1, mockPlayer1);
        playersMap.put(userId2, mockPlayer2);

        matchId = UUID.randomUUID();

        gameState = new GameState(matchId, playersMap, mockBoard, initialTurnEnds);
    }

    @Nested
    @DisplayName("Testes de Inicialização e Turno")
    class TurnLifecycleTests {

        @Test
        @DisplayName("Deve inicializar com versão 1 e turnOrder populado com ID dos jogadores")
        void shouldInitializeWithVersionOneAndTurnOrder() {
            assertEquals(matchId, gameState.getMatchId());
            assertEquals(1, gameState.getVersion());
            assertEquals(initialTurnEnds, gameState.getCurrentTurnEnds());

            UUID currentPlayer = gameState.currentPlayerTurn();
            assertTrue(currentPlayer.equals(userId1) || currentPlayer.equals(userId2));
        }

        @Test
        @DisplayName("Deve alternar o turno de forma circular, incrementar versão e decrementar duração dos efeitos")
        void shouldRotateTurnsCircularlyAndDecrementEffects() {
            UUID firstPlayer = gameState.currentPlayerTurn();
            Instant nextTurnEnds = Instant.now().plusSeconds(30);

            gameState.nextTurn(nextTurnEnds);

            assertEquals(2, gameState.getVersion(), "A versão do estado deve subir após alteração");
            assertEquals(nextTurnEnds, gameState.getCurrentTurnEnds(), "O tempo limite deve ser atualizado");
            assertNotEquals(firstPlayer, gameState.currentPlayerTurn(), "O turno deve passar para o próximo jogador");

            verify(mockPlayer1, times(1)).decrementEffectDuration();
            verify(mockPlayer2, times(1)).decrementEffectDuration();
        }

        @Test
        @DisplayName("Deve validar corretamente se o tempo do turno atual expirou")
        void shouldCheckIfTurnIsExpired() {
            Instant pastTime = Instant.now().plusSeconds(60);
            Instant insideLimitTime = Instant.now().plusSeconds(10);

            assertTrue(gameState.isTurnExpired(pastTime), "Deveria acusar que o turno expirou");
            assertFalse(gameState.isTurnExpired(insideLimitTime), "Ainda está dentro do tempo do turno");
        }
    }

    @Nested
    @DisplayName("Testes de Validação de Jogadores")
    class PlayerValidationTests {

        @Test
        @DisplayName("Deve lançar PlayerNotInGameException ao buscar jogador que não pertence à partida")
        void shouldThrowExceptionWhenPlayerNotFound() {
            assertThrows(PlayerNotInGameException.class, () -> gameState.getPlayerOrThrow(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Deve retornar o Player correto se ele estiver mapeado no jogo")
        void shouldReturnPlayerWhenHeExists() {
            Player found = gameState.getPlayerOrThrow(userId1);
            assertEquals(mockPlayer1, found);
        }

        @Test
        @DisplayName("Deve remover o jogador do mapa de participantes com sucesso")
        void shouldRemovePlayerFromMap() {
            assertEquals(2, gameState.getPlayers().size());

            gameState.removePlayer(userId1);

            assertEquals(1, gameState.getPlayers().size());
            assertFalse(gameState.getPlayers().containsKey(userId1));
        }
    }

    @Nested
    @DisplayName("Testes de Fim de Jogo")
    class GameOverTests {

        @Test
        @DisplayName("Deve encerrar a partida porque um jogador saiu")
        void shouldFinishBecausePlayerLeft() {
            Optional<GameOver> result = gameState.gameOverBecausePlayerLeft(userId2);

            assertTrue(result.isPresent());

            GameOver gameOver = result.get();

            assertEquals(GameOverReasons.PLAYER_LEFT, gameOver.reason());
            assertEquals(mockPlayer1, gameOver.winner());
            assertEquals(mockPlayer2, gameOver.loser());
        }

        @Test
        @DisplayName("Deve encerrar a partida porque um jogador desconectou")
        void shouldFinishBecausePlayerDisconnected() {
            Optional<GameOver> result = gameState.gameOverBecauseDisconnection(userId2);

            assertTrue(result.isPresent());

            GameOver gameOver = result.get();

            assertEquals(GameOverReasons.PLAYER_DISCONNECTED, gameOver.reason());
            assertEquals(mockPlayer1, gameOver.winner());
            assertEquals(mockPlayer2, gameOver.loser());
        }

        @Test
        @DisplayName("Não deve encerrar a partida se ninguém alcançou 3 pontos")
        void shouldNotFinishWhenNobodyReachedThreePoints() {
            when(mockPlayer1.getScore()).thenReturn(1);
            when(mockPlayer2.getScore()).thenReturn(2);

            Optional<GameOver> result = gameState.gameOverBecauseScore();

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Deve declarar Player 1 vencedor ao atingir 3 pontos")
        void shouldFinishWhenPlayerOneReachesThreePoints() {
            when(mockPlayer1.getScore()).thenReturn(3);

            Optional<GameOver> result = gameState.gameOverBecauseScore();

            assertTrue(result.isPresent());

            GameOver gameOver = result.get();

            assertEquals(GameOverReasons.SCORE, gameOver.reason());
            assertEquals(mockPlayer1, gameOver.winner());
            assertEquals(mockPlayer2, gameOver.loser());
        }

        @Test
        @DisplayName("Deve declarar Player 2 vencedor ao atingir 3 pontos")
        void shouldFinishWhenPlayerTwoReachesThreePoints() {
            when(mockPlayer1.getScore()).thenReturn(1);
            when(mockPlayer2.getScore()).thenReturn(3);

            Optional<GameOver> result = gameState.gameOverBecauseScore();

            assertTrue(result.isPresent());

            GameOver gameOver = result.get();

            assertEquals(GameOverReasons.SCORE, gameOver.reason());
            assertEquals(mockPlayer2, gameOver.winner());
            assertEquals(mockPlayer1, gameOver.loser());
        }

        @Test
        @DisplayName("Não deve encerrar a partida por AFK antes de 3 turnos")
        void shouldNotFinishBecauseAfkBeforeLimit() {
            when(mockPlayer1.getPassedTurn()).thenReturn(2);
            when(mockPlayer2.getPassedTurn()).thenReturn(1);

            Optional<GameOver> result = gameState.gameOverBecauseAfk();

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Deve encerrar a partida quando Player 2 ficar AFK")
        void shouldFinishWhenPlayerOneIsAfk() {
            when(mockPlayer1.getPassedTurn()).thenReturn(0);
            when(mockPlayer2.getPassedTurn()).thenReturn(3);

            Optional<GameOver> result = gameState.gameOverBecauseAfk();

            assertTrue(result.isPresent());

            GameOver gameOver = result.get();

            assertEquals(GameOverReasons.PLAYER_AFK, gameOver.reason());
            assertEquals(mockPlayer1, gameOver.winner());
            assertEquals(mockPlayer2, gameOver.loser());
        }

        @Test
        @DisplayName("Deve encerrar a partida quando Player 2 ficar AFK")
        void shouldFinishWhenPlayerTwoIsAfk() {
            when(mockPlayer1.getPassedTurn()).thenReturn(0);
            when(mockPlayer2.getPassedTurn()).thenReturn(3);

            Optional<GameOver> result = gameState.gameOverBecauseAfk();

            assertTrue(result.isPresent());

            GameOver gameOver = result.get();

            assertEquals(GameOverReasons.PLAYER_AFK, gameOver.reason());
            assertEquals(mockPlayer1, gameOver.winner());
            assertEquals(mockPlayer2, gameOver.loser());
        }
    }
}