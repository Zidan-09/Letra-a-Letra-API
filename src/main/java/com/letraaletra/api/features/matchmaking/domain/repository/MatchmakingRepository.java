package com.letraaletra.api.features.matchmaking.domain.repository;

import com.letraaletra.api.shared.domain.repository.RemoveUser;
import com.letraaletra.api.shared.domain.repository.UserIsOnQueue;

public interface MatchmakingRepository extends
        EnqueueUser,
        RemoveUser,
        PollPair,
        UserIsOnQueue
{}
