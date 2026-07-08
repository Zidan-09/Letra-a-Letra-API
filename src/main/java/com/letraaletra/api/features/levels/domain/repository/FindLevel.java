package com.letraaletra.api.features.levels.domain.repository;

import com.letraaletra.api.features.levels.domain.Level;

import java.util.Optional;
import java.util.UUID;

public interface FindLevel {
    Optional<Level> find(UUID id);
}
