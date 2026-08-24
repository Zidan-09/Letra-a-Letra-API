package com.letraaletra.api.features.game.infrastructure.websocket.assembler;

import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameOverMapper;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankedMatchResult;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingMatchResultMapper;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingOverResultMapper;
import com.letraaletra.api.shared.infrastructure.websocket.WsConnectionRegistry;
import com.letraaletra.api.features.game.infrastructure.websocket.assembler.GameResponseAssembler;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import org.springframework.web.socket.WebSocketSession;

public class GameResponseAssemblerService implements GameResponseAssembler {
    private final UserRepository userRepository;
    private final WsConnectionRegistry connectionRegistry;

    public GameResponseAssemblerService(
            UserRepository userRepository,
            WsConnectionRegistry connectionRegistry
    ) {
        this.userRepository = userRepository;
        this.connectionRegistry = connectionRegistry;
    }

    @Override
    public WsResponse assembleGameOver(Game game, GameOver gameOver, HandledGameOver handledGameOver) {
        User winner = userRepository.find(gameOver.winner().getUserId())
                .orElseThrow(UserNotFoundException::new);

        User loser = userRepository.find(gameOver.loser().getUserId())
                .orElseThrow(UserNotFoundException::new);

        WebSocketSession winnerSession = connectionRegistry.findByUserId(winner.getUserId());
        WebSocketSession loserSession = connectionRegistry.findByUserId(loser.getUserId());

        Participant winnerParticipant = Participant.create(
                winner,
                winnerSession != null ? winnerSession.getId() : ""
        );

        Participant loserParticipant = Participant.create(
                loser,
                loserSession != null ? loserSession.getId() : ""
        );

        if (game.getGameType().equals(GameType.RANKING)) {
            RankedMatchResult winnerResult = RankingMatchResultMapper.toResponse(
                    gameOver.winner(),
                    winnerParticipant,
                    handledGameOver.winnerPoints().orElseThrow()
            );

            RankedMatchResult loserResult = RankingMatchResultMapper.toResponse(
                    gameOver.loser(),
                    loserParticipant,
                    handledGameOver.loserPoints().orElseThrow()
            );

            return RankingOverResultMapper.toResponse(winnerResult, loserResult);
        }

        return GameOverMapper.toResponse(
                gameOver,
                winnerParticipant,
                loserParticipant
        );
    }
}
