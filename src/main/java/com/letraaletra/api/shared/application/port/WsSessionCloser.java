package com.letraaletra.api.shared.application.port;

import java.util.UUID;

public interface WsSessionCloser {
    void closeByUserId(UUID userId);
}
