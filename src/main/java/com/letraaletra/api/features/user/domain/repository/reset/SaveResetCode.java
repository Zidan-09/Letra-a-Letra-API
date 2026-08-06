package com.letraaletra.api.features.user.domain.repository.reset;

import com.letraaletra.api.features.user.domain.PasswordResetCode;

public interface SaveResetCode {
    void save(PasswordResetCode passwordResetCode);
}
