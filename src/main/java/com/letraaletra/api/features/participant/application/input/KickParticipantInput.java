package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record KickParticipantInput(
        String token,
        UUID target,
        UUID user
) {
}
