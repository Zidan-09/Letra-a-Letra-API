package com.letraaletra.api.shared.infrastructure.websocket.broadcast;

import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameOverMapper;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.ranking.application.service.UpdateRankingPointsService;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankedMatchResult;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingMatchResultMapper;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingOverMapper;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.shared.application.port.GameResponseAssembler;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

public class GameResponseAssemblerService implements GameResponseAssembler {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UpdateRankingPointsService rankingPointsService;

    public GameResponseAssemblerService(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            UpdateRankingPointsService rankingPointsService
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.rankingPointsService = rankingPointsService;
    }

    @Override
    public WsResponse assembleGameOver(Game game, GameOver gameOver) {
        User winner = userRepository.find(gameOver.winner().getUserId())
                .orElseThrow(UserNotFoundException::new);

        User loser = userRepository.find(gameOver.loser().getUserId())
                .orElseThrow(UserNotFoundException::new);

        Participant winnerParticipant = Participant.create(
                winner,
                sessionRepository.findByUserId(winner.getId()).getId()
        );

        Participant loserParticipant = Participant.create(
                loser,
                sessionRepository.findByUserId(loser.getId()).getId()
        );

        if (game.getGameType().equals(GameType.RANKING)) {
            UpdateRankingPoints winnerPoints = rankingPointsService.handle(
                    winner,
                    gameOver.winner().getScore(),
                    gameOver.loser().getScore()
            );

            UpdateRankingPoints loserPoints = rankingPointsService.handle(
                    loser,
                    gameOver.loser().getScore(),
                    gameOver.winner().getScore()
            );

            RankedMatchResult winnerResult = RankingMatchResultMapper.toDto(
                    gameOver.winner(),
                    winnerParticipant,
                    winnerPoints
            );

            RankedMatchResult loserResult = RankingMatchResultMapper.toDto(
                    gameOver.loser(),
                    loserParticipant,
                    loserPoints
            );

            return RankingOverMapper.toResponse(winnerResult, loserResult);
        }

        return GameOverMapper.toResponse(
                gameOver,
                winnerParticipant,
                loserParticipant
        );
    }
}
