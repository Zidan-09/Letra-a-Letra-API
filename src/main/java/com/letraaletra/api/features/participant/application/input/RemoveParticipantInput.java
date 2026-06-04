package com.letraaletra.api.features.participant.application.input;

public record RemoveParticipantInput(
        String gameId,
        String userId
) {
}
