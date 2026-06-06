package com.letraaletra.api.features.participant.application.input;

public record KickParticipantInput(
        String token,
        String target,
        String user
) {
}
