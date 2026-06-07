package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardView;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardViewBuilder;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardDTOMapper;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardViewDTOMapper;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.WordDTOMapper;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.PlayerDTOMapper;

import java.util.Arrays;

public class GameStateDTOMapper {
    public static GameStateDTO toDto(Game game, String viewerId) {
        BoardView boardView = BoardViewBuilder.build(
                game.getGameState(),
                viewerId
        );

        return new GameStateDTO(
                game.getGameState().getPlayers().values().stream()
                        .map(player -> PlayerDTOMapper.toDTO(
                                player,
                                game.getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                BoardViewDTOMapper.toDTO(boardView),
                Arrays.stream(game.getGameState().getBoard().words()).map(WordDTOMapper::toDTO).toList(),
                game.getGameState().currentPlayerTurn()
        );
    }

    public static GameStateDTO toGlobalDto(Game game) {
        return new GameStateDTO(
                game.getGameState().getPlayers().values().stream()
                        .map(player -> PlayerDTOMapper.toDTO(
                                player,
                                game.getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                BoardDTOMapper.toDTO(game.getGameState().getBoard()),
                Arrays.stream(game.getGameState().getBoard().words()).map(WordDTOMapper::toDTO).toList(),
                game.getGameState().currentPlayerTurn()
        );
    }
}
