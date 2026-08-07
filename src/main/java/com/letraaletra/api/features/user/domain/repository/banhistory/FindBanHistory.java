package com.letraaletra.api.features.user.domain.repository.banhistory;

import com.letraaletra.api.features.user.domain.ban.BanHistory;

import java.util.Optional;
import java.util.UUID;

public interface FindBanHistory {
    Optional<BanHistory> findById(UUID banHistoryId);
    Optional<BanHistory> findActiveByUserId(UUID userId);
}
