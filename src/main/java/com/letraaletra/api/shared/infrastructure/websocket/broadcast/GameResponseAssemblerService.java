package com.letraaletra.api.shared.infrastructure.websocket.broadcast;

import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameOverMapper;
import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.ranking.application.service.UpdateRankingPointsService;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankedMatchResult;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingMatchResultMapper;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingOverMapper;
import com.letraaletra.api.shared.application.port.GameResponseAssembler;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

public class GameResponseAssemblerService implements GameResponseAssembler {
    private final UserRepository userRepository;
    private final UpdateRankingPointsService rankingPointsService;

    public GameResponseAssemblerService(
            UserRepository userRepository,
            UpdateRankingPointsService rankingPointsService
    ) {
        this.userRepository = userRepository;
        this.rankingPointsService = rankingPointsService;
    }

    @Override
    public WsResponse assembleGameOver(Game game, GameOverResult gameOverResult) {
        if (game.getGameType().equals(GameType.RANKING)) {
            User winner = userRepository.find(gameOverResult.winner().getUserId())
                    .orElseThrow(UserNotFoundException::new);

            User loser = userRepository.find(gameOverResult.loser().getUserId())
                    .orElseThrow(UserNotFoundException::new);

            UpdateRankingPoints winnerPoints = rankingPointsService.handle(
                    winner,
                    gameOverResult.winner().getScore(),
                    gameOverResult.loser().getScore()
            );

            UpdateRankingPoints loserPoints = rankingPointsService.handle(
                    loser,
                    gameOverResult.loser().getScore(),
                    gameOverResult.winner().getScore()
            );

            RankedMatchResult winnerResult = RankingMatchResultMapper.toDto(
                    gameOverResult.winner(),
                    game.getParticipantByUserId(winner.getId()),
                    winnerPoints
            );

            RankedMatchResult loserResult = RankingMatchResultMapper.toDto(
                    gameOverResult.loser(),
                    game.getParticipantByUserId(loser.getId()),
                    loserPoints
            );

            return RankingOverMapper.toResponse(winnerResult, loserResult);
        }

        return GameOverMapper.toResponse(gameOverResult, game);
    }
}
