package com.letraaletra.api.features.admin.domain.repository;

import java.util.UUID;

public interface InvalidateAdminToken {
    void invalidateAllByAdminId(UUID adminId);
}
