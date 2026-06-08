package com.letraaletra.api.features.cosmetic.domain.repository;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;

import java.util.Optional;

public interface FindCosmetic {
    Optional<Cosmetic> find(String cosmeticId);
}
