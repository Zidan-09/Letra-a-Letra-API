package com.letraaletra.api.features.game.domain.history;

import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;

import java.util.List;
import java.util.UUID;

public record GameHistory(
        UUID roomId,
        String roomName,
        GameType type,
        GameStatus status,
        List<MatchHistory> matches
) {}
