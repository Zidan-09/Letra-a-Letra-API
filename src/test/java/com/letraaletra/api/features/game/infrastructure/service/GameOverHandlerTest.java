package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.game.domain.GameOverReasons;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.ranking.application.port.RankingPointsService;
import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.user.application.port.UserStatsService;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameOverHandlerTest {

    @Mock private GameRepository gameRepository;
    @Mock private UserRepository userRepository;
    @Mock private ActorManager<Game> actorManager;
    @Mock private RoomTimeoutManager roomTimeoutManager;
    @Mock private UserStatsService userStatsService;
    @Mock private RankingPointsService rankingPointsService;
    @Mock private Game game;
    @Mock private BusinessAuditRecorder auditRecorder;

    @Captor
    private ArgumentCaptor<List<User>> usersCaptor;

    @InjectMocks
    private GameOverHandler handler;

    private User winnerUser;
    private User loserUser;
    private Player winnerPlayer;
    private Player loserPlayer;
    private GameOver gameOver;

    @BeforeEach
    void setUp() {
        winnerUser = UserFactory.createLocal("winner", "winner@test.com", "hash");
        loserUser = UserFactory.createLocal("loser", "loser@test.com", "hash");

        winnerPlayer = mock(Player.class);
        loserPlayer = mock(Player.class);

        when(winnerPlayer.getUserId()).thenReturn(winnerUser.getUserId());
        when(loserPlayer.getUserId()).thenReturn(loserUser.getUserId());

        gameOver = new GameOver(GameOverReasons.SCORE, winnerPlayer, loserPlayer);

        when(userRepository.findUsersById(anyList()))
                .thenReturn(List.of(winnerUser, loserUser));

        lenient().doNothing().when(auditRecorder).record(any(AuditEvent.class));
    }

    @Test
    @DisplayName("Partida ranqueada deve aplicar pontos de ranking antes de persistir e retornar os deltas")
    void rankedMatchShouldApplyRankingPointsBeforePersistingAndReturnDeltas() {
        when(game.getGameType()).thenReturn(GameType.RANKING);
        when(game.getGameStatus()).thenReturn(GameStatus.CLOSED);
        when(winnerPlayer.getScore()).thenReturn(3);
        when(loserPlayer.getScore()).thenReturn(2);

        UpdateRankingPoints winnerDelta = new UpdateRankingPoints(100, 10, 110);
        UpdateRankingPoints loserDelta = new UpdateRankingPoints(80, -15, 65);

        when(rankingPointsService.handle(eq(winnerUser), eq(3), eq(2))).thenReturn(winnerDelta);
        when(rankingPointsService.handle(eq(loserUser), eq(2), eq(3))).thenReturn(loserDelta);

        HandledGameOver handled = handler.handle(game, gameOver);

        InOrder inOrder = inOrder(userStatsService, rankingPointsService, userRepository, gameRepository);
        inOrder.verify(userStatsService).update(winnerUser, true);
        inOrder.verify(userStatsService).update(loserUser, false);
        inOrder.verify(rankingPointsService).handle(winnerUser, 3, 2);
        inOrder.verify(rankingPointsService).handle(loserUser, 2, 3);
        inOrder.verify(userRepository, times(2)).saveAll(anyList());
        inOrder.verify(gameRepository).save(game);

        assertTrue(handled.winnerPoints().isPresent());
        assertEquals(winnerDelta, handled.winnerPoints().get());
        assertTrue(handled.loserPoints().isPresent());
        assertEquals(loserDelta, handled.loserPoints().get());

        verify(actorManager).remove(game.getId());
        verify(roomTimeoutManager, never()).start(game);
    }

    @Test
    @DisplayName("Partida custom deve voltar a sala para WAITING sem aplicar ranking")
    void customMatchShouldReturnRoomToWaitingWithoutRanking() {
        when(game.getGameType()).thenReturn(GameType.CUSTOM);
        when(game.getGameStatus()).thenReturn(GameStatus.WAITING);

        HandledGameOver handled = handler.handle(game, gameOver);

        verify(userStatsService).update(winnerUser, true);
        verify(userStatsService).update(loserUser, false);
        verify(rankingPointsService, never())
                .handle(any(User.class), anyInt(), anyInt());
        verify(roomTimeoutManager).start(game);
        verify(actorManager, never()).remove(game.getId());

        assertEquals(HandledGameOver.withoutRanking(), handled);
    }

    @Test
    @DisplayName("Deve salvar os usuarios nos momentos esperados do fluxo com ambos os jogadores")
    void shouldPersistFullAggregateOnceWithBothPlayers() {
        when(game.getGameType()).thenReturn(GameType.RANKING);
        when(game.getGameStatus()).thenReturn(GameStatus.CLOSED);
        when(winnerPlayer.getScore()).thenReturn(3);
        when(loserPlayer.getScore()).thenReturn(1);
        when(rankingPointsService.handle(any(User.class), anyInt(), anyInt()))
                .thenReturn(new UpdateRankingPoints(0, 0, 0));

        handler.handle(game, gameOver);

        verify(userRepository, times(2)).saveAll(usersCaptor.capture());
        verify(userRepository, never()).save(winnerUser);
        verify(userRepository, never()).save(loserUser);

        List<List<User>> capturedCalls = usersCaptor.getAllValues();
        assertEquals(2, capturedCalls.size());
        assertTrue(capturedCalls.get(0).containsAll(List.of(winnerUser, loserUser)));
        assertTrue(capturedCalls.get(1).containsAll(List.of(winnerUser, loserUser)));
    }
}