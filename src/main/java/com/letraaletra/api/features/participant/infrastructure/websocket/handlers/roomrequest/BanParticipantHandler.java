package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.features.participant.application.port.ParticipantNotifier;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.BanParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ModerationResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.BanParticipantMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

@Component
public class BanParticipantHandler implements RoomRequestHandler<BanParticipantWsRequest> {
    private final UseCase<BanParticipantInput, BanParticipantOutput> useCase;
    private final ParticipantNotifier participantNotifier;

    public BanParticipantHandler(
            UseCase<BanParticipantInput, BanParticipantOutput> useCase,
            ParticipantNotifier participantNotifier
    ) {
        this.useCase = useCase;
        this.participantNotifier = participantNotifier;
    }

    @Transactional
    @Override
    public void handle(BanParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        BanParticipantInput input = BanParticipantMapper.toInput(request, userId);

        BanParticipantOutput output = useCase.execute(input);

        BanParticipantResponse dto = BanParticipantMapper.toResponse(output);

        List<String> socketIds = output.game().getParticipants().getParticipants().stream()
                .map(Participant::getSocketId)
                .toList();

        participantNotifier.notifyAll(socketIds, dto);

        ModerationResponse dtoForBanned = new ModerationResponse("Banned from game");

        participantNotifier.notifyUser(UUID.fromString(request.participantId()), dtoForBanned);

    }

    @Override
    public Class<BanParticipantWsRequest> getType() {
        return BanParticipantWsRequest.class;
    }
}
