package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameDTOMapper {
    public GameDTO toDTO(Game game, String tokenGameId, List<ParticipantDTO> participantDTOS) {
        return new GameDTO(
                tokenGameId,
                game.getRoomName(),
                participantDTOS,
                game.getPositions()
        );
    }
}
