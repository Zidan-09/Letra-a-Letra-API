package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.game.usecase.KickParticipantUseCase;
import com.letraaletra.api.presentation.dto.request.websocket.KickParticipantWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class KickParticipantHandler implements RoomRequestHandler<KickParticipantWsRequest> {
    @Autowired
    private KickParticipantUseCase kickParticipant;

    @Override
    public void handle(KickParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        kickParticipant.execute(
                request.tokenGameId(),
                request.participantId(),
                userId
        );
    }

    @Override
    public Class<KickParticipantWsRequest> getType() {
        return KickParticipantWsRequest.class;
    }
}
