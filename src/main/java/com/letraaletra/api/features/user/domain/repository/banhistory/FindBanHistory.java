package com.letraaletra.api.features.user.domain.repository.banhistory;

import com.letraaletra.api.features.user.domain.BanHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindBanHistory {
    Optional<BanHistory> findById(UUID banHistoryId);
    Optional<BanHistory> findActiveByUserId(UUID userId);
}
