package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record UnbanParticipantInput(
        String token,
        UUID target,
        UUID user
) {
}
