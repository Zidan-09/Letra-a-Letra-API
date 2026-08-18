package com.letraaletra.api.features.cosmetic.domain.repository;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;

import java.util.Optional;
import java.util.UUID;

public interface FindCosmetic {
    Optional<Cosmetic> find(UUID cosmeticId);
    Optional<Cosmetic> findByName(String name);
}
