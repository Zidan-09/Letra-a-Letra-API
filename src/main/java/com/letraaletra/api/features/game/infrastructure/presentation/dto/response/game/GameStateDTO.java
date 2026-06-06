package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game;

import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.BoardDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.word.WordDTO;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerDTO;

import java.util.List;

public record GameStateDTO(
        List<PlayerDTO> players,
        BoardDTO[][] board,
        List<WordDTO> words,
        String currentTurnPlayerId
) {}
