package com.letraaletra.api.features.game.domain.factory;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.RoomSettings;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.service.BoardGenerator;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameFactoriesTest {

    @Nested
    @DisplayName("Testes da DefaultGameFactory")
    class DefaultGameFactoryTests {

        private final DefaultGameFactory factory = new DefaultGameFactory();

        @Test
        @DisplayName("Deve gerar um jogo de Matchmaking com dois jogadores e configurações padrão de privacidade")
        void shouldGenerateDefaultGameForMatchmaking() {
            Participant player1 = new Participant("p1-id", "sess-1", "PlayerOne", List.of());
            Participant player2 = new Participant("p2-id", "sess-2", "PlayerTwo", List.of());
            String roomCode = "MATCH1";

            DefaultGameResult result = factory.generate(player1, player2, roomCode);

            assertNotNull(result);
            Game game = result.game();
            assertNotNull(game);

            assertEquals(roomCode, game.getCode());
            assertEquals(GameType.MATCHMAKING, game.getGameType());
            assertEquals("default-name", game.getRoomName());
            assertEquals("p1-id", game.getHostId(), "O player1 deve iniciar como Host");
            assertEquals(2, game.getAmountPlayers(), "A sala deve possuir exatamente 2 jogadores ativos");

            RoomSettings settings = game.getRoomSettings();
            assertTrue(settings.roomAllowSpectators());
            assertTrue(settings.isPrivateGame());
        }
    }

    @Nested
    @DisplayName("Testes da GameStateFactory")
    class GameStateFactoryTests {

        private final GameStateFactory factory = new GameStateFactory();
        @Mock private Board mockBoard;

        @Test
        @DisplayName("Deve gerar o GameState mapeando estritamente participantes que possuem o papel de PLAYER")
        void shouldGenerateGameStateOnlyFromActivePlayers() {
            Participant p1 = new Participant("player-1", "s1", "P1", List.of());
            p1.changeRole(ParticipantRole.PLAYER);

            Participant p2 = new Participant("player-2", "s2", "P2", List.of());
            p2.changeRole(ParticipantRole.PLAYER);

            Participant spectator = new Participant("spec-3", "s3", "S3", List.of());
            spectator.changeRole(ParticipantRole.SPECTATOR);

            List<Participant> roomParticipants = List.of(p1, p2, spectator);

            GameState gameState = factory.generate(roomParticipants, mockBoard);

            assertNotNull(gameState);
            assertNotNull(gameState.getMatchId(), "Deve gerar um UUID válido para a partida");
            assertEquals(mockBoard, gameState.getBoard());

            var playersMap = gameState.getPlayers();
            assertEquals(2, playersMap.size(), "Deveria mapear apenas os 2 usuários com role PLAYER");
            assertTrue(playersMap.containsKey("player-1"));
            assertTrue(playersMap.containsKey("player-2"));
            assertFalse(playersMap.containsKey("spec-3"), "O espectador não deve entrar no mapa de execução de turnos");

            assertNotNull(gameState.getCurrentTurnEnds(), "O timestamp limite do primeiro turno deve ser definido");
        }
    }

    @Nested
    @DisplayName("Testes da DefaultGameStateFactory")
    class DefaultGameStateFactoryTests {

        @Mock private GameStateFactory gameStateFactory;
        @Mock private BoardGenerator boardGenerator;
        @Mock private Game mockGame;
        @Mock private Board mockBoard;
        @Mock private GameState mockGameState;

        @InjectMocks
        private DefaultGameStateFactory defaultGameStateFactory;

        @Test
        @DisplayName("Deve orquestrar a geração do tabuleiro e delegar a montagem do GameState")
        void shouldOrchestrateBoardAndStateGeneration() {
            GameMode gameMode = GameMode.NORMAL;
            List<String> words = List.of("java", "spring", "godot");
            List<Participant> mockParticipantsList = List.of();

            when(mockGame.getParticipants()).thenReturn(mockParticipantsList);
            when(boardGenerator.generate(words, gameMode)).thenReturn(mockBoard);
            when(gameStateFactory.generate(mockParticipantsList, mockBoard)).thenReturn(mockGameState);

            GameState resultState = defaultGameStateFactory.generate(mockGame, gameMode, words);

            assertNotNull(resultState);
            assertEquals(mockGameState, resultState);

            verify(boardGenerator, times(1)).generate(words, gameMode);
            verify(gameStateFactory, times(1)).generate(mockParticipantsList, mockBoard);
        }
    }
}