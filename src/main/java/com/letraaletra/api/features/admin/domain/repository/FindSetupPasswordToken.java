package com.letraaletra.api.features.admin.domain.repository;

import com.letraaletra.api.features.admin.domain.AdminPasswordSetupToken;

import java.util.Optional;

public interface FindSetupPasswordToken {
    Optional<AdminPasswordSetupToken> findByTokenHash(String token);
}
