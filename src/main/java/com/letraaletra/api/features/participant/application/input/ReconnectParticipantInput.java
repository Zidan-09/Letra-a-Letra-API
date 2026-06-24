package com.letraaletra.api.features.participant.application.input;

import java.util.UUID;

public record ReconnectParticipantInput(
        UUID user,
        String session
) {
}
