package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.ExpireTurnTimeoutResult;

import java.util.Optional;
import java.util.UUID;

public interface ExpireTurnService {
    Optional<ExpireTurnTimeoutResult> expire(UUID gameId, int version);
}
