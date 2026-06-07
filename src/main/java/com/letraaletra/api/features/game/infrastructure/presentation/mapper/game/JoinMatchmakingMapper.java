package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.matchmaking.application.output.JoinMatchmakingOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.matchmaking.MatchmakingUser;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingStatus;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.JoinMatchmakingResponse;

public class JoinMatchmakingMapper {
    public static JoinMatchmakingInput toInput(String user, String session, GameMode gameMode) {
        MatchmakingUser matchmakingUser = new MatchmakingUser(user, session);

        return new JoinMatchmakingInput(matchmakingUser, gameMode);
    }

    public static JoinMatchmakingResponse toResponse(JoinMatchmakingOutput output) {
        Game game = output.game().orElse(null);
        String token = output.gameTokenId().orElse(null);

        return new JoinMatchmakingResponse(
                game != null ? MatchmakingStatus.FOUNDED : MatchmakingStatus.SEARCHING,
                game != null ? game.getGameState().getCurrentTurnEnds() : null,
                token,
                game != null ? GameStateDTOMapper.toGlobalDto(game) : null
        );
    }
}
