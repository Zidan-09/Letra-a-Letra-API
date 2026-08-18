package com.letraaletra.api.shared.application.port;

import com.letraaletra.api.shared.domain.QueueMatch;

import java.util.Optional;

public interface QueuePairProvider {
    Optional<QueueMatch> get();
}
