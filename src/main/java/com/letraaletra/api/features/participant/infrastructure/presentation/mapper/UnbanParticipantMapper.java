package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.UnbanParticipantInput;
import com.letraaletra.api.features.participant.application.output.UnbanParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.UnbanParticipantResponse;

public class UnbanParticipantMapper {
    public static UnbanParticipantInput toInput(UnbanParticipantWsRequest request, String userId) {
        return new UnbanParticipantInput(
                request.tokenGameId(),
                request.userId(),
                userId
        );
    }

    public static UnbanParticipantResponse toResponse(UnbanParticipantOutput output) {
        return new UnbanParticipantResponse(

        );
    }
}
