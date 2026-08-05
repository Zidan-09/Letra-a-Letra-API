package com.letraaletra.api.features.admin.domain.repository;

import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface FindAdminResetToken {
    Optional<AdminPasswordResetToken> findById(UUID id);
    Optional<AdminPasswordResetToken> findLatestByAdminId(UUID adminId);
    Optional<AdminPasswordResetToken> findByTokenHash(String tokenHash);
}
