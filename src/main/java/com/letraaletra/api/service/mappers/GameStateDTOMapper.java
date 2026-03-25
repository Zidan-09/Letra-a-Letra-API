package com.letraaletra.api.service.mappers;

import com.letraaletra.api.dto.response.game.BoardDTO;
import com.letraaletra.api.dto.response.game.GameStateDTO;
import com.letraaletra.api.dto.response.player.PlayerDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameStateDTOMapper {
    public GameStateDTO toDTO(List<PlayerDTO> playerDTOS, BoardDTO[][] boardDTO, String currentPlayerTurn) {
        return new GameStateDTO(
                playerDTOS,
                boardDTO,
                currentPlayerTurn
        );
    }
}
