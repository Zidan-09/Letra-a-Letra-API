package com.letraaletra.api.features.matchmaking.domain.repository;

public interface MatchmakingRepository extends
        EnqueueUser,
        RemoveUser,
        PollPair,
        UserIsOnQueue
{}
