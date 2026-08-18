package com.letraaletra.api.features.user.domain.reset.repository;

import com.letraaletra.api.features.user.domain.reset.PasswordResetCode;

public interface SaveResetCode {
    void save(PasswordResetCode passwordResetCode);
}
