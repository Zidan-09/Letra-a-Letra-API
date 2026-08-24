package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.SwapPositionInput;
import com.letraaletra.api.features.participant.application.output.SwapPositionOutput;
import com.letraaletra.api.features.participant.application.port.ParticipantNotifier;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.SwapPositionWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.SwapPositionResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.SwapPositionMapper;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SwapRoomPlaceHandler implements RoomRequestHandler<SwapPositionWsRequest> {
    private final UseCase<SwapPositionInput, SwapPositionOutput> useCase;
    private final ParticipantNotifier participantNotifier;

    private SwapRoomPlaceHandler(
            UseCase<SwapPositionInput, SwapPositionOutput> useCase,
            ParticipantNotifier participantNotifier
    ) {
        this.useCase = useCase;
        this.participantNotifier = participantNotifier;
    }

    @Override
    public void handle(SwapPositionWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        SwapPositionInput input = SwapPositionMapper.toInput(request, userId);

        SwapPositionOutput output = useCase.execute(input);

        SwapPositionResponse dto = SwapPositionMapper.toResponse(output);

        List<String> socketIds = output.game().getParticipants().getParticipants().stream()
                .map(Participant::getSocketId)
                .toList();

        participantNotifier.notifyAll(socketIds, dto);
    }

    @Override
    public Class<SwapPositionWsRequest> getType() {
        return SwapPositionWsRequest.class;
    }
}
