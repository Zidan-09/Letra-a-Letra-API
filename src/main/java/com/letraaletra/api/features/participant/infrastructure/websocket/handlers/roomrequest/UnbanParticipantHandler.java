package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.UnbanParticipantInput;
import com.letraaletra.api.features.participant.application.output.UnbanParticipantOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import com.letraaletra.api.features.participant.application.usecase.UnbanUserUseCase;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.UnbanParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.UnbanParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class UnbanParticipantHandler implements RoomRequestHandler<UnbanParticipantWsRequest> {

    @Autowired
    private UnbanUserUseCase unbanUser;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(UnbanParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        UnbanParticipantInput command = UnbanParticipantMapper.toInput(request, userId);

        UnbanParticipantOutput output = unbanUser.execute(command);

        UnbanParticipantResponse dto = UnbanParticipantMapper.toResponse(output);

        gameNotifier.notifierOne(userId, dto);
    }

    @Override
    public Class<UnbanParticipantWsRequest> getType() {
        return UnbanParticipantWsRequest.class;
    }
}
