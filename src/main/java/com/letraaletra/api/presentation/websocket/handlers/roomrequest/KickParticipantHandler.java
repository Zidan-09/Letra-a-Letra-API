package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.participant.KickParticipantCommand;
import com.letraaletra.api.application.output.participant.KickParticipantOutput;
import com.letraaletra.api.application.usecase.participant.KickParticipantUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.infrastructure.websocket.SessionRepository;
import com.letraaletra.api.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.KickParticipantResponseDTO;
import com.letraaletra.api.presentation.dto.response.websocket.ModerationResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.KickParticipantMapper;
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
    private KickParticipantMapper kickParticipantMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameNotifier broadcastService;

    private final Logger logger = LoggerFactory.getLogger(KickParticipantHandler.class);

    @Override
    public void handle(KickParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        KickParticipantCommand command = kickParticipantMapper.toCommand(request, userId);

        KickParticipantOutput output = kickParticipant.execute(command);

        KickParticipantResponseDTO dto = kickParticipantMapper.toResponseDTO(output);

        broadcastService.send(output.game(), dto);

        WebSocketSession target = sessionRepository.findByUserId(request.participantId());

        String json = objectMapper.writeValueAsString(
                new ModerationResponseDTO("Kicked from game")
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
