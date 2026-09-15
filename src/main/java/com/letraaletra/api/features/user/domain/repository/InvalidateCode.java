package com.letraaletra.api.features.user.domain.repository;

import java.util.UUID;

public interface InvalidateCode {
    void invalidateAllByUserId(UUID userId);
}
