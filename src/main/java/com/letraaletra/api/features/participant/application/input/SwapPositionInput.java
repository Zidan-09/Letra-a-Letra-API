package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record SwapPositionInput(
        String token,
        Integer position,
        UUID user
) {}
