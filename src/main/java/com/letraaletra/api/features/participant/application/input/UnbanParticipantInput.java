package com.letraaletra.api.features.participant.application.input;

public record UnbanParticipantInput(
        String token,
        String target,
        String user
) {
}
