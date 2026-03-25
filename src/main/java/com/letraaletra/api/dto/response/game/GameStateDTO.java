package com.letraaletra.api.dto.response.game;

import com.letraaletra.api.dto.response.player.PlayerDTO;

import java.util.List;

public record GameStateDTO(
        List<PlayerDTO> players,
        BoardDTO[][] board,
        String currentTurnPlayerId
) {}
