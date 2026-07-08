package com.letraaletra.api.features.cosmetic.domain.repository;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;

import java.util.UUID;

public interface DeleteCosmetic {
    void delete(Cosmetic cosmetic);
}
