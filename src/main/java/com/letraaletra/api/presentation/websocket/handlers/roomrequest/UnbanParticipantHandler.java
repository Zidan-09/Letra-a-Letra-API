package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.game.usecase.UnbanUserUseCase;
import com.letraaletra.api.presentation.dto.request.websocket.UnbanParticipantWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class UnbanParticipantHandler implements RoomRequestHandler<UnbanParticipantWsRequest> {

    @Autowired
    private UnbanUserUseCase unbanUser;

    @Override
    public void handle(UnbanParticipantWsRequest request, WebSocketSession session) {
        String hostId = (String) session.getAttributes().get("userId");
        String targetId = request.userId();

        unbanUser.execute(
                request.tokenGameId(),
                targetId,
                hostId
        );
    }

    @Override
    public Class<UnbanParticipantWsRequest> getType() {
        return UnbanParticipantWsRequest.class;
    }
}
