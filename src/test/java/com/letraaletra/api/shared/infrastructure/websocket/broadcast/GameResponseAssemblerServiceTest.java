package com.letraaletra.api.shared.infrastructure.websocket.broadcast;

import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.game.domain.GameOverReasons;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GameOverResultResponse;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankedMatchResult;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankingOverResultResponse;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameResponseAssemblerServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private SessionRepository sessionRepository;
    @Mock private Game game;

    private GameResponseAssemblerService assembler;

    private User winnerUser;
    private User loserUser;
    private com.letraaletra.api.features.player.domain.Player winnerPlayer;
    private com.letraaletra.api.features.player.domain.Player loserPlayer;

    @BeforeEach
    void setUp() {
        assembler = new GameResponseAssemblerService(userRepository, sessionRepository);

        winnerUser = UserFactory.createLocal("winner", "winner@test.com", "hash");
        loserUser = UserFactory.createLocal("loser", "loser@test.com", "hash");

        winnerPlayer = org.mockito.Mockito.mock(com.letraaletra.api.features.player.domain.Player.class);
        loserPlayer = org.mockito.Mockito.mock(com.letraaletra.api.features.player.domain.Player.class);
    }

    private GameOver buildGameOver() {
        when(winnerPlayer.getUserId()).thenReturn(winnerUser.getUserId());
        when(loserPlayer.getUserId()).thenReturn(loserUser.getUserId());
        when(winnerPlayer.getScore()).thenReturn(3);
        when(loserPlayer.getScore()).thenReturn(2);

        return new GameOver(GameOverReasons.SCORE, winnerPlayer, loserPlayer);
    }

    @Test
    @DisplayName("Deve montar resposta ranqueada usando apenas os deltas recebidos, sem executar regras de negócio")
    void shouldAssembleRankingResponseFromProvidedDeltasWithoutBusinessSideEffects() {
        GameOver gameOver = buildGameOver();
        when(game.getGameType()).thenReturn(GameType.RANKING);
        when(userRepository.find(winnerUser.getUserId())).thenReturn(Optional.of(winnerUser));
        when(userRepository.find(loserUser.getUserId())).thenReturn(Optional.of(loserUser));

        HandledGameOver handled = HandledGameOver.withRanking(
                new com.letraaletra.api.features.ranking.domain.UpdateRankingPoints(100, 10, 110),
                new com.letraaletra.api.features.ranking.domain.UpdateRankingPoints(80, -15, 65)
        );

        Object response = assembler.assembleGameOver(game, gameOver, handled);

        RankingOverResultResponse rankingResponse = (RankingOverResultResponse) response;
        RankedMatchResult winnerResult = rankingResponse.data().winner();
        RankedMatchResult loserResult = rankingResponse.data().loser();

        assertEquals(100, winnerResult.previousRankingPoints());
        assertEquals(10, winnerResult.pointsChanged());
        assertEquals(110, winnerResult.currentRankingPoints());

        assertEquals(80, loserResult.previousRankingPoints());
        assertEquals(-15, loserResult.pointsChanged());
        assertEquals(65, loserResult.currentRankingPoints());

        verify(userRepository).find(winnerUser.getUserId());
        verify(userRepository).find(loserUser.getUserId());
        verify(sessionRepository).findByUserId(winnerUser.getUserId());
        verify(sessionRepository).findByUserId(loserUser.getUserId());
        verifyNoMoreInteractions(userRepository, sessionRepository, game);
    }

    @Test
    @DisplayName("Partida custom deve montar resposta padrão sem dados de ranking")
    void shouldAssembleDefaultResponseForCustomMatch() {
        GameOver gameOver = buildGameOver();
        when(game.getGameType()).thenReturn(GameType.CUSTOM);
        when(userRepository.find(winnerUser.getUserId())).thenReturn(Optional.of(winnerUser));
        when(userRepository.find(loserUser.getUserId())).thenReturn(Optional.of(loserUser));

        Object response = assembler.assembleGameOver(game, gameOver, HandledGameOver.withoutRanking());

        assertEquals(GameOverResultResponse.class, response.getClass());

        verify(sessionRepository).findByUserId(winnerUser.getUserId());
        verify(sessionRepository).findByUserId(loserUser.getUserId());
        verifyNoMoreInteractions(sessionRepository, game);
    }
}
