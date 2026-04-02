package com.letraaletra.api.presentation.mappers.game;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.presentation.mappers.board.BoardDTOMapper;
import com.letraaletra.api.presentation.mappers.board.WordDTOMapper;
import com.letraaletra.api.presentation.mappers.player.PlayerDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class GameStateDTOMapper {
    @Autowired
    private BoardDTOMapper boardDTOMapper;

    @Autowired
    private WordDTOMapper wordDTOMapper;

    @Autowired
    private PlayerDTOMapper playerDTOMapper;

    public GameStateDTO toDTO(Game game) {
        return new GameStateDTO(
                game.getGameState().getPlayers().values().stream()
                        .map(player -> playerDTOMapper.toDTO(
                                player,
                                game.getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                boardDTOMapper.toDTO(game.getGameState().getBoard()),
                Arrays.stream(game.getGameState().getBoard().words()).map(word -> wordDTOMapper.toDTO(word)).toList(),
                game.getGameState().currentPlayerTurn()
        );
    }
}
