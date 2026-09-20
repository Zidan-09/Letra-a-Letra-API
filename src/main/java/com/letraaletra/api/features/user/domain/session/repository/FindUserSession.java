package com.letraaletra.api.features.user.domain.session.repository;

import com.letraaletra.api.features.user.domain.session.UserSession;

import java.util.Optional;
import java.util.UUID;

public interface FindUserSession {
    Optional<UserSession> findByUserId(UUID userId);

    Optional<UserSession> findByUserIdForUpdate(UUID userId);

    Optional<UserSession> findByTokenHashForUpdate(String tokenHash);
}
