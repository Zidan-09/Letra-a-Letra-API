package com.letraaletra.api.features.game.application.port;

import com.letraaletra.api.features.game.domain.ExpireTurnResult;

import java.util.Optional;
import java.util.UUID;

public interface ExpireTurnService {
    Optional<ExpireTurnResult> expire(UUID gameId, int version);
}
