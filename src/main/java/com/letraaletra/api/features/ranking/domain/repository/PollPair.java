package com.letraaletra.api.features.ranking.domain.repository;

import com.letraaletra.api.shared.domain.QueueMatch;

import java.util.Optional;

public interface PollPair {
    Optional<QueueMatch> pollPair();
}
