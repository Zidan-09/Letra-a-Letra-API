package com.letraaletra.api.features.participant.application.input;

public record ReconnectParticipantInput(
        String user,
        String session
) {
}
