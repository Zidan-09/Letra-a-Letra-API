package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.usecase.participant.KickParticipantUseCase;
import com.letraaletra.api.domain.repository.SessionRepository;
import com.letraaletra.api.presentation.dto.request.websocket.KickParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.ModerationWsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class KickParticipantHandler implements RoomRequestHandler<KickParticipantWsRequest> {
    @Autowired
    private KickParticipantUseCase kickParticipant;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(KickParticipantHandler.class);

    @Override
    public void handle(KickParticipantWsRequest request, WebSocketSession session) {
        String hostId = (String) session.getAttributes().get("userId");
        String targetId = request.participantId();

        kickParticipant.execute(
                request.tokenGameId(),
                targetId,
                hostId
        );

        WebSocketSession target = sessionRepository.findByUserId(targetId);

        String json = objectMapper.writeValueAsString(
                new ModerationWsResponse("Kicked from game")
        );

        try {
            target.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.warn("Error to send message to {}: {}", target.getId(), e.getMessage());
        }
    }

    @Override
    public Class<KickParticipantWsRequest> getType() {
        return KickParticipantWsRequest.class;
    }
}
