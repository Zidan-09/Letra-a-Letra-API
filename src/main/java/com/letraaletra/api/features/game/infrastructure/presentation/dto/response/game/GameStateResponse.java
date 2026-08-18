package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.word.WordResponse;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.player.PlayerResponse;

import java.util.List;

public record GameStateResponse(
        List<PlayerResponse> players,
        BoardResponse[][] board,
        List<WordResponse> words,
        String currentTurnPlayerId
) {}
