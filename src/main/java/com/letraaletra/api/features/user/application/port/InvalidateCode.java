package com.letraaletra.api.features.user.application.port;

import java.util.UUID;

public interface InvalidateCode {
    void invalidateAllByUserId(UUID userId);
}
