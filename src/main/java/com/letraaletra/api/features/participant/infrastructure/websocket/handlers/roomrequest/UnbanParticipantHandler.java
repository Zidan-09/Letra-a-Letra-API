package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.UnbanParticipantInput;
import com.letraaletra.api.features.participant.application.output.UnbanParticipantOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.UnbanParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.UnbanParticipantMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
public class UnbanParticipantHandler implements RoomRequestHandler<UnbanParticipantWsRequest> {
    private final UseCase<UnbanParticipantInput, UnbanParticipantOutput> useCase;
    private final GameNotifier gameNotifier;

    public UnbanParticipantHandler(
            UseCase<UnbanParticipantInput, UnbanParticipantOutput> unbanUser,
            GameNotifier gameNotifier
    ) {
        this.useCase = unbanUser;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(UnbanParticipantWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        UnbanParticipantInput command = UnbanParticipantMapper.toInput(request, userId);

        UnbanParticipantOutput output = useCase.execute(command);

        UnbanParticipantResponse dto = UnbanParticipantMapper.toResponse(output);

        gameNotifier.notifierOne(userId, dto);
    }

    @Override
    public Class<UnbanParticipantWsRequest> getType() {
        return UnbanParticipantWsRequest.class;
    }
}
