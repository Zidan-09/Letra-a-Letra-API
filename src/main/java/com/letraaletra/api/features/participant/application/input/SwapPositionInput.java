package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record SwapPositionInput(
        UUID gameId,
        Integer position,
        UUID user
) {}
