package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record KickParticipantInput(
        UUID gameId,
        UUID target,
        UUID user
) {
}
