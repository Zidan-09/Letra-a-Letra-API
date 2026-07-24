package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.features.game.domain.state.MatchHistory;

import java.util.List;
import java.util.UUID;

public record GameHistory(
        UUID roomId,
        String roomName,
        GameType type,
        GameStatus status,
        List<MatchHistory> matches
) {}
