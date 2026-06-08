package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.user.application.service.UpdateStatsService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameOverHandlerTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private GameTimeoutManager gameTimeoutManager;

    @Mock
    private UpdateStatsService updateStatsService;

    @InjectMocks
    private GameOverHandler handler;

    @Test
    void shouldHandleFinishedGameAndUpdateStats() {
        Game game = mock(Game.class);
        GameOverResult result = mock(GameOverResult.class);

        Player winner = mock(Player.class);
        Player loser = mock(Player.class);

        when(result.finished()).thenReturn(true);
        when(result.winner()).thenReturn(winner);
        when(result.loser()).thenReturn(loser);

        when(game.getGameType()).thenReturn(GameType.CUSTOM);
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);

        handler.handle(game, result);

        verify(updateStatsService).execute(winner, true);
        verify(updateStatsService).execute(loser, false);
        verify(gameRepository).save(game);
    }

    @Test
    void shouldNotProcessWhenGameNotFinished() {
        Game game = mock(Game.class);
        GameOverResult result = mock(GameOverResult.class);

        when(result.finished()).thenReturn(false);

        handler.handle(game, result);

        verifyNoInteractions(updateStatsService);
        verifyNoInteractions(gameRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(actorManager);
        verifyNoInteractions(gameTimeoutManager);
    }

    @Test
    void shouldRestartTimeoutWhenGameIsCustom() {
        Game game = mock(Game.class);
        GameOverResult result = mock(GameOverResult.class);

        Player winner = mock(Player.class);
        Player loser = mock(Player.class);

        when(result.finished()).thenReturn(true);
        when(result.winner()).thenReturn(winner);
        when(result.loser()).thenReturn(loser);

        when(game.getGameType()).thenReturn(GameType.CUSTOM);
        when(game.getGameStatus()).thenReturn(GameStatus.RUNNING);

        handler.handle(game, result);

        verify(gameTimeoutManager).start(game);
        verify(gameRepository).save(game);
    }

    @Test
    void shouldRemoveActorsAndUsersWhenGameIsClosed() {
        Game game = mock(Game.class);
        GameOverResult result = mock(GameOverResult.class);

        Player winner = mock(Player.class);
        Player loser = mock(Player.class);

        var participant = mock(com.letraaletra.api.features.participant.domain.Participant.class);
        User user = mock(User.class);

        when(result.finished()).thenReturn(true);
        when(result.winner()).thenReturn(winner);
        when(result.loser()).thenReturn(loser);

        when(game.getGameType()).thenReturn(GameType.CUSTOM);
        when(game.getGameStatus()).thenReturn(GameStatus.CLOSED);

        when(game.getId()).thenReturn("game-1");
        when(game.getParticipants()).thenReturn(List.of(participant));

        when(participant.getUserId()).thenReturn("user-1");
        when(userRepository.find("user-1")).thenReturn(Optional.of(user));

        handler.handle(game, result);

        verify(actorManager).remove("game-1");
        verify(user).leaveGame();
        verify(userRepository).save(user);
        verify(gameRepository).save(game);
    }
}