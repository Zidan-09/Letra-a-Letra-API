package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record UnbanParticipantInput(
        UUID gameId,
        UUID target,
        UUID user
) {
}
