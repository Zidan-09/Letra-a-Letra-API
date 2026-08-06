package com.letraaletra.api.features.user.domain.repository.reset;

import com.letraaletra.api.features.user.domain.PasswordResetCode;

import java.util.Optional;
import java.util.UUID;

public interface FindResetCode {
    Optional<PasswordResetCode> findById(UUID id);
    Optional<PasswordResetCode> findByCodeHash(String codeHash);
}
