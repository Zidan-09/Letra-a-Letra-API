package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerActionResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateDTOMapper;
import org.springframework.stereotype.Component;

@Component
public class PlayerActionMapper {
    public static PlayerActionInput toInput(String token, String userId, GameAction action) {
        return new PlayerActionInput(
                token,
                userId,
                action
        );
    }

    public static PlayerActionResponse toResponse(PlayerActionOutput output, String viewer) {
        return new PlayerActionResponse(
                output.game().getGameState().getCurrentTurnEnds(),
                output.events(),
                GameStateDTOMapper.toDto(output.game(), viewer)
        );
    }

    public static PlayerActionResponse toGlobalResponse(PlayerActionOutput output) {
        return new PlayerActionResponse(
                output.game().getGameState().getCurrentTurnEnds(),
                output.events(),
                GameStateDTOMapper.toGlobalDto(output.game())
        );
    }
}
