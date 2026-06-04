package com.letraaletra.api.features.participant.application.input;

public record SwapPositionInput(
        String token,
        Integer position,
        String user
) {}
