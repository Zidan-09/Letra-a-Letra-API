package com.letraaletra.api.features.participant.infrastructure.presentation.mapper;

import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.BanParticipantResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameDTOMapper;
import org.springframework.stereotype.Component;

@Component
public class BanParticipantMapper {
    public BanParticipantInput toCommand(BanParticipantWsRequest request, String user) {
        return new BanParticipantInput(
                request.tokenGameId(),
                request.participantId(),
                user
        );
    }

    public BanParticipantResponse toResponseDTO(BanParticipantOutput output) {
        return new BanParticipantResponse(
               GameDTOMapper.toDTO(output.game(), output.token())
        );
    }
}
