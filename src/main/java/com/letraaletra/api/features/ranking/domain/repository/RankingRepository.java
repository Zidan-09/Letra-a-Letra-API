package com.letraaletra.api.features.ranking.domain.repository;

import com.letraaletra.api.shared.domain.repository.RemoveUser;
import com.letraaletra.api.shared.domain.repository.UserIsOnQueue;

public interface RankingRepository extends
        EnqueueUser,
        RemoveUser,
        PollPair,
        UserIsOnQueue
{}
