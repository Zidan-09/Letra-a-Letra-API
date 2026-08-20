package com.letraaletra.api.features.queue.domain.repository;

import com.letraaletra.api.features.queue.domain.QueueMatch;
import com.letraaletra.api.features.queue.domain.QueueType;

import java.util.Optional;

public interface PollPair {
    Optional<QueueMatch> pollPair(QueueType type);
}
