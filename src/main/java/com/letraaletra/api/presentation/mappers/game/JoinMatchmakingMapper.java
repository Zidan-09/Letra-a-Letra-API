package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.matchmaking.application.output.JoinMatchmakingOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.domain.matchmaking.MatchmakingUser;
import com.letraaletra.api.presentation.dto.response.game.MatchmakingStatus;
import com.letraaletra.api.presentation.dto.response.websocket.JoinMatchmakingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinMatchmakingMapper {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public JoinMatchmakingInput toCommand(String user, String session, GameMode gameMode) {
        MatchmakingUser matchmakingUser = new MatchmakingUser(user, session);

        return new JoinMatchmakingInput(matchmakingUser, gameMode);
    }

    public JoinMatchmakingResponse toResponseDTO(JoinMatchmakingOutput output) {
        Game game = output.game().orElse(null);
        String token = output.gameTokenId().orElse(null);

        return new JoinMatchmakingResponse(
                game != null ? MatchmakingStatus.FOUNDED : MatchmakingStatus.SEARCHING,
                game != null ? game.getGameState().getCurrentTurnEnds() : null,
                token,
                game != null ? gameStateDTOMapper.toAllDTO(game) : null
        );
    }
}
