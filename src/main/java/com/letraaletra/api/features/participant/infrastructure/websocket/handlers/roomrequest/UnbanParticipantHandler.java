package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.UnbanParticipantInput;
import com.letraaletra.api.features.participant.application.output.UnbanParticipantOutput;
import com.letraaletra.api.features.participant.application.port.ParticipantNotifier;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.UnbanParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.UnbanParticipantMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

@Component
public class UnbanParticipantHandler implements RoomRequestHandler<UnbanParticipantWsRequest> {
    private final UseCase<UnbanParticipantInput, UnbanParticipantOutput> useCase;
    private final ParticipantNotifier participantNotifier;

    public UnbanParticipantHandler(
            UseCase<UnbanParticipantInput, UnbanParticipantOutput> unbanUser,
            ParticipantNotifier participantNotifier
    ) {
        this.useCase = unbanUser;
        this.participantNotifier = participantNotifier;
    }

    @Override
    public void handle(UnbanParticipantWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        UnbanParticipantInput input = UnbanParticipantMapper.toInput(request, userId);

        UnbanParticipantOutput output = useCase.execute(input);

        UnbanParticipantResponse dto = UnbanParticipantMapper.toResponse(output);

        participantNotifier.notifyUser(userId, dto);
    }

    @Override
    public Class<UnbanParticipantWsRequest> getType() {
        return UnbanParticipantWsRequest.class;
    }
}
