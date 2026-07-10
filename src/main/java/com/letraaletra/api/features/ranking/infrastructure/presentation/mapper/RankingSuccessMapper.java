package com.letraaletra.api.features.ranking.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateDTOMapper;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankSuccessResponse;

public class RankingSuccessMapper {
    public static RankSuccessResponse toResponse(Game game) {
        return new RankSuccessResponse(
                MatchmakingStatus.FOUNDED,
                game.getGameState().getCurrentTurnEnds(),
                game.getId().toString(),
                GameStateDTOMapper.toGlobalDto(game)
        );
    }
}
