package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.board.Board;
import com.letraaletra.api.domain.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.GameMode;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.domain.game.service.GameOverChecker;
import com.letraaletra.api.domain.game.service.GameOverResult;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.player.Player;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.presentation.dto.mappers.PlayerDTOMapper;
import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameOverWsResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GameOverServiceTest {
    @Mock
    private GameOverChecker gameOverChecker;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerDTOMapper playerDTOMapper;

    private final BoardGenerator boardGenerator = new BoardGenerator();

    @InjectMocks
    private GameOverService gameOverService;

    private final RoomSettings settings = new RoomSettings(true, false);
    private final Participant host = new Participant("uid", "sid", "nickname", "avatar", ParticipantRole.PLAYER);
    private final Game game = new Game("id", "code", "gameName", settings, host);

    @Test
    @DisplayName("Should build a Game Over response if game finished")
    void execute() {
        Map<String, Player> players = new HashMap<>();

        Player player1 = new Player("id1");
        Player player2 = new Player("id2");

        players.put(player1.getUserId(), player1);
        players.put(player2.getUserId(), player2);

        Board board = boardGenerator.generate(
                List.of("test1", "test2", "test3", "test4", "test5"),
                GameMode.NORMAL
        );

        GameState state = new GameState(players, board);
        game.updateGameState(state);

        GameOverResult result = new GameOverResult(true, player1, player2);

        Mockito.when(gameOverChecker.evaluate(state))
                .thenReturn(result);

        User user1 = new User("id1", "nickname1", "avatar", "test1@email.com", "hash");
        User user2 = new User("id2", "nickname2", "avatar", "test2@email.com", "hash");

        Mockito.when(userRepository.find("id1")).thenReturn(user1);
        Mockito.when(userRepository.find("id2")).thenReturn(user2);

        PlayerDTO dto1 = new PlayerDTO("id1", "nickname1", "avatar", 0, List.of());
        PlayerDTO dto2 = new PlayerDTO("id2", "nickname2", "avatar", 0, List.of());

        Mockito.when(playerDTOMapper.toDTO(player1, user1)).thenReturn(dto1);
        Mockito.when(playerDTOMapper.toDTO(player2, user2)).thenReturn(dto2);

        Optional<GameOverWsResponse> response = gameOverService.buildIfFinished(game);

        Assertions.assertTrue(response.isPresent());
    }

    @Test
    @DisplayName("Should not return Game Over Response")
    void returnNull() {
        Map<String, Player> players = new HashMap<>();

        Player player1 = new Player("id1");
        Player player2 = new Player("id2");

        players.put(player1.getUserId(), player1);
        players.put(player2.getUserId(), player2);

        Board board = boardGenerator.generate(
                List.of("test1", "test2", "test3", "test4", "test5"),
                GameMode.NORMAL
        );

        GameState state = new GameState(players, board);
        game.updateGameState(state);

        GameOverResult result = new GameOverResult(false, null, null);

        Assertions.assertFalse(result.finished());
    }

    @Test
    @DisplayName("Should throw an UserNotFoundException")
    void throwBecauseUser() {
        Map<String, Player> players = new HashMap<>();

        Player player1 = new Player("id1");
        Player player2 = new Player("id2");

        players.put(player1.getUserId(), player1);
        players.put(player2.getUserId(), player2);

        Board board = boardGenerator.generate(
                List.of("test1", "test2", "test3", "test4", "test5"),
                GameMode.NORMAL
        );

        GameState state = new GameState(players, board);
        game.updateGameState(state);

        GameOverResult result = new GameOverResult(true, player1, player2);

        Mockito.when(gameOverChecker.evaluate(state))
                .thenReturn(result);

        Mockito.when(userRepository.find("id1"))
                .thenReturn(null);

        Assertions.assertThrows(UserNotFoundException.class, () -> gameOverService.buildIfFinished(game));
    }

}