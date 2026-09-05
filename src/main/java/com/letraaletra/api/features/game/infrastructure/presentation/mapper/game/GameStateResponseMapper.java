package com.letraaletra.api.features.game.infrastructure.presentation.mapper.game;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.exception.GameNotRunningException;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardView;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardResponseMapper;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardViewBuilder;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.BoardViewResponseMapper;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.board.WordResponseMapper;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.PlayerResponseMapper;

import java.util.Arrays;
import java.util.UUID;

public class GameStateResponseMapper {
    public static GameStateResponse toResponse(Game game, UUID viewerId) {
        GameState state = requireRunningState(game);

        BoardView boardView = BoardViewBuilder.build(
                state,
                viewerId
        );

        return new GameStateResponse(
                state.getPlayers().values().stream()
                        .map(player -> PlayerResponseMapper.toResponse(
                                player,
                                game.getParticipants().getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                BoardViewResponseMapper.toResponse(boardView),
                Arrays.stream(state.getBoard().words()).map(WordResponseMapper::toResponse).toList(),
                state.currentPlayerTurn().toString()
        );
    }

    public static GameStateResponse toGlobalResponse(Game game) {
        GameState state = requireRunningState(game);

        return new GameStateResponse(
                state.getPlayers().values().stream()
                        .map(player -> PlayerResponseMapper.toResponse(
                                player,
                                game.getParticipants().getParticipantByUserId(player.getUserId())
                        ))
                        .toList(),
                BoardResponseMapper.toResponse(state.getBoard()),
                Arrays.stream(state.getBoard().words()).map(WordResponseMapper::toResponse).toList(),
                state.currentPlayerTurn().toString()
        );
    }

    private static GameState requireRunningState(Game game) {
        GameState state = game.getGameState();
        if (state == null || game.getGameStatus() != GameStatus.RUNNING) {
            throw new GameNotRunningException();
        }
        return state;
    }
}
