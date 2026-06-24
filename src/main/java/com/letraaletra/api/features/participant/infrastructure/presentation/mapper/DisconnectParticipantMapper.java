package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.DisconnectParticipantResponse;

import java.util.UUID;

public class DisconnectParticipantMapper {
    public static DisconnectParticipantInput toInput(String user, String session) {
        return new DisconnectParticipantInput(
                UUID.fromString(user),
                session
        );
    }

    public static DisconnectParticipantResponse toResponse(DisconnectParticipantOutput output) {
        return new DisconnectParticipantResponse(
            output.user().toString()
        );
    }
}
