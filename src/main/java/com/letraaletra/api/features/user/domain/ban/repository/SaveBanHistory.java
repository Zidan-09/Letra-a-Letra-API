package com.letraaletra.api.features.user.domain.ban.repository;

import com.letraaletra.api.features.user.domain.ban.BanHistory;

public interface SaveBanHistory {
    void save(BanHistory banHistory);
}
