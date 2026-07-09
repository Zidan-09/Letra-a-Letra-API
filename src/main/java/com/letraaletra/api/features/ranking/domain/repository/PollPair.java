package com.letraaletra.api.features.ranking.domain.repository;

import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;

import java.util.Optional;

public interface PollPair {
    Optional<MatchmakingPair> pollPair();
}
