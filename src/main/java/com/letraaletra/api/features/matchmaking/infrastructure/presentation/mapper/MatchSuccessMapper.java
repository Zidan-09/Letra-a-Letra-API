package com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateResponseMapper;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.MatchSuccessResponse;

public class MatchSuccessMapper {
    public static MatchSuccessResponse toResponse(Game game) {
        return new MatchSuccessResponse(
                MatchmakingStatus.FOUNDED,
                game.getGameState().getCurrentTurnEnds(),
                game.getId().toString(),
                GameStateResponseMapper.toGlobalResponse(game)
        );
    }
}
