package com.letraaletra.api.features.admin.domain.repository;

import com.letraaletra.api.features.admin.domain.AdminPasswordResetToken;

public interface SaveAdminResetToken {
    void save(AdminPasswordResetToken adminPasswordResetToken);
}
