package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.domain.actions.GameAction;
import com.letraaletra.api.presentation.dto.response.websocket.PlayerActionResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameStateDTOMapper;
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

    public PlayerActionResponseDTO toResponseDTO(PlayerActionOutput output, String viewer) {
        return new PlayerActionResponseDTO(
                output.game().getGameState().getCurrentTurnEnds(),
                output.events(),
                gameStateDTOMapper.toDTO(output.game(), viewer)
        );
    }

    public PlayerActionResponseDTO toAllResponseDTO(PlayerActionOutput output) {
        return new PlayerActionResponseDTO(
                output.game().getGameState().getCurrentTurnEnds(),
                output.events(),
                gameStateDTOMapper.toAllDTO(output.game())
        );
    }
}
