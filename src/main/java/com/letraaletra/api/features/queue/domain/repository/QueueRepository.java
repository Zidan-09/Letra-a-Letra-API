package com.letraaletra.api.features.queue.domain.repository;

public interface QueueRepository extends
        EnqueueUser,
        PollPair,
        RemoveUser,
        UserIsOnQueue
{}
