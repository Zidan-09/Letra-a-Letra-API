package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record RemoveParticipantInput(
        UUID gameId,
        UUID userId
) {
}
