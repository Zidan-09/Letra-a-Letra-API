package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerActionResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameStateDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerActionMapper {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    public PlayerActionInput toCommand(String token, String userId, GameAction action) {
        return new PlayerActionInput(
                token,
                userId,
                action
        );
    }

    public PlayerActionResponse toResponseDTO(PlayerActionOutput output, String viewer) {
        return new PlayerActionResponse(
                output.game().getGameState().getCurrentTurnEnds(),
                output.events(),
                gameStateDTOMapper.toDTO(output.game(), viewer)
        );
    }

    public PlayerActionResponse toAllResponseDTO(PlayerActionOutput output) {
        return new PlayerActionResponse(
                output.game().getGameState().getCurrentTurnEnds(),
                output.events(),
                gameStateDTOMapper.toAllDTO(output.game())
        );
    }
}
