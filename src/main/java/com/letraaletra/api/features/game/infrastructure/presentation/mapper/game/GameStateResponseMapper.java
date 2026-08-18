package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardView;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardViewBuilder;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardResponseMapper;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardViewResponseMapper;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.WordResponseMapper;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.PlayerResponseMapper;

import java.util.Arrays;
import java.util.UUID;

public class GameStateResponseMapper {
    public static GameStateResponse toResponse(Game game, UUID viewerId) {
        BoardView boardView = BoardViewBuilder.build(
                game.getGameState(),
                viewerId
        );

        return new GameStateResponse(
                game.getGameState().getPlayers().values().stream()
                        .map(player -> PlayerResponseMapper.toResponse(
                                player,
                                game.getParticipants().getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                BoardViewResponseMapper.toResponse(boardView),
                Arrays.stream(game.getGameState().getBoard().words()).map(WordResponseMapper::toResponse).toList(),
                game.getGameState().currentPlayerTurn().toString()
        );
    }

    public static GameStateResponse toGlobalResponse(Game game) {
        return new GameStateResponse(
                game.getGameState().getPlayers().values().stream()
                        .map(player -> PlayerResponseMapper.toResponse(
                                player,
                                game.getParticipants().getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                BoardResponseMapper.toResponse(game.getGameState().getBoard()),
                Arrays.stream(game.getGameState().getBoard().words()).map(WordResponseMapper::toResponse).toList(),
                game.getGameState().currentPlayerTurn().toString()
        );
    }
}
