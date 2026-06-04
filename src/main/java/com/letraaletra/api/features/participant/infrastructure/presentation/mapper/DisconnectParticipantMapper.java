package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.DisconnectParticipantResponse;
import org.springframework.stereotype.Component;

@Component
public class DisconnectParticipantMapper {
    public DisconnectParticipantInput toCommand(String user, String session) {
        return new DisconnectParticipantInput(
                user,
                session
        );
    }

    public DisconnectParticipantResponse toResponseDTO(DisconnectParticipantOutput output) {
        return new DisconnectParticipantResponse(
            output.user()
        );
    }
}
