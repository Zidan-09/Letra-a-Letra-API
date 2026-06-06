package com.letraaletra.api.features.participant.application.input;

public record BanParticipantInput(
        String token,
        String target,
        String user
) {
}
