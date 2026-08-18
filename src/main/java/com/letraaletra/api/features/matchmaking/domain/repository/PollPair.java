package com.letraaletra.api.features.matchmaking.domain.repository;

import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.shared.domain.QueueMatch;

import java.util.Optional;

public interface PollPair {
    Optional<QueueMatch> pollPair();
}
