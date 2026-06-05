package com.letraaletra.api.features.game.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.word.WordDTO;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerResponse;

import java.util.List;

public record GameStateDTO(
        List<PlayerResponse> players,
        BoardDTO[][] board,
        List<WordDTO> words,
        String currentTurnPlayerId
) {}
