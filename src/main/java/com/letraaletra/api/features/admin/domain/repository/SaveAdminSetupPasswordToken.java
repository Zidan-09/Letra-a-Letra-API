package com.letraaletra.api.features.admin.domain.repository;

import com.letraaletra.api.features.admin.domain.AdminPasswordSetupToken;

public interface SaveAdminSetupPasswordToken {
    void save(AdminPasswordSetupToken adminPasswordSetupToken);
}
