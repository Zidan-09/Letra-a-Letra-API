package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record DisconnectParticipantInput(
        UUID user,
        String session
) {
}
