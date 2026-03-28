package com.letraaletra.api.presentation.dto.mappers;

import com.letraaletra.api.domain.board.Word;
import com.letraaletra.api.presentation.dto.response.game.BoardDTO;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameStateDTOMapper {
    public GameStateDTO toDTO(List<PlayerDTO> playerDTOS, BoardDTO[][] boardDTO, List<Word> words, String currentPlayerTurn) {
        return new GameStateDTO(
                playerDTOS,
                boardDTO,
                words,
                currentPlayerTurn
        );
    }
}
