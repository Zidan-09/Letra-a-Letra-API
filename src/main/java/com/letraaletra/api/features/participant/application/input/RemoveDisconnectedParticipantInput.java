package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record RemoveDisconnectedParticipantInput(
        UUID gameId,
        UUID userId
) {
}
