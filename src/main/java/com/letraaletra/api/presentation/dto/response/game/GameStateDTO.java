package com.letraaletra.api.presentation.dto.response.game;

import com.letraaletra.api.domain.board.Word;
import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;

import java.util.List;

public record GameStateDTO(
        List<PlayerDTO> players,
        BoardDTO[][] board,
        List<Word> words,
        String currentTurnPlayerId
) {}
