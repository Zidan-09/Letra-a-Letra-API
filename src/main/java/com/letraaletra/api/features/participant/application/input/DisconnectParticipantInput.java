package com.letraaletra.api.features.participant.application.input;

public record DisconnectParticipantInput(
        String user,
        String session
) {
}
