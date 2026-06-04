package com.letraaletra.api.presentation.dto.response.game;

import com.letraaletra.api.presentation.dto.response.game.board.BoardDTO;
import com.letraaletra.api.presentation.dto.response.game.board.word.WordDTO;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerResponse;

import java.util.List;

public record GameStateDTO(
        List<PlayerResponse> players,
        BoardDTO[][] board,
        List<WordDTO> words,
        String currentTurnPlayerId
) {}
