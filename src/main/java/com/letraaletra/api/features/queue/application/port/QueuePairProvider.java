package com.letraaletra.api.features.queue.application.port;

import com.letraaletra.api.features.queue.domain.QueueMatch;

import java.util.Optional;

public interface QueuePairProvider {
    Optional<QueueMatch> get();
}
