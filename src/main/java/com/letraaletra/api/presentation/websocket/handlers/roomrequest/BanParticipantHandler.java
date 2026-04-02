package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.participant.BanParticipantCommand;
import com.letraaletra.api.application.output.participant.BanParticipantOutput;
import com.letraaletra.api.application.usecase.participant.BanParticipantUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.infrastructure.websocket.SessionRepository;
import com.letraaletra.api.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.BanParticipantResponseDTO;
import com.letraaletra.api.presentation.dto.response.websocket.ModerationResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.BanParticipantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class BanParticipantHandler implements RoomRequestHandler<BanParticipantWsRequest> {
    @Autowired
    private BanParticipantUseCase banParticipant;

    @Autowired
    private BanParticipantMapper banParticipantMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameNotifier broadcastService;

    private final Logger logger = LoggerFactory.getLogger(BanParticipantHandler.class);

    @Override
    public void handle(BanParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        BanParticipantCommand command = banParticipantMapper.toCommand(request, userId);

        BanParticipantOutput output = banParticipant.execute(command);

        BanParticipantResponseDTO dto = banParticipantMapper.toResponseDTO(output);

        broadcastService.send(output.game(), dto);

        WebSocketSession target = sessionRepository.findByUserId(request.participantId());

        String json = objectMapper.writeValueAsString(
                new ModerationResponseDTO("Banned from game")
        );

        try {
            target.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.warn("Error to send message to {}: {}", target.getId(), e.getMessage());
        }

    }

    @Override
    public Class<BanParticipantWsRequest> getType() {
        return BanParticipantWsRequest.class;
    }
}
