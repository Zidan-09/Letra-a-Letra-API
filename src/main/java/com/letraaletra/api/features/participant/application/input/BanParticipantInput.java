package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record BanParticipantInput(
        UUID gameId,
        UUID target,
        UUID user
) {
}
