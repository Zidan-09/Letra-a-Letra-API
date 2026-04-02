package com.letraaletra.api.presentation.websocket.handlers.participant;

import com.letraaletra.api.application.command.participant.ReconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.ReconnectParticipantOutput;
import com.letraaletra.api.application.usecase.participant.ReconnectUseCase;
import com.letraaletra.api.presentation.dto.response.websocket.ReconnectParticipantResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.ReconnectParticipantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;

@Component
public class ReconnectParticipantHandler {
    @Autowired
    private ReconnectParticipantMapper reconnectParticipantMapper;

    @Autowired
    private ReconnectUseCase reconnectUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(ReconnectParticipantHandler.class);

    public void handle(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        ReconnectParticipantCommand command = reconnectParticipantMapper.toCommand(userId, session.getId());

        Optional<ReconnectParticipantOutput> output = reconnectUseCase.execute(command);

        output.ifPresent(out -> {
            ReconnectParticipantResponseDTO dto = reconnectParticipantMapper.toResponseDTO(out);

            send(session, dto);
        });
    }

    private void send(WebSocketSession session, ReconnectParticipantResponseDTO dto) {
        try {
            session.sendMessage(
                    new TextMessage(
                            objectMapper.writeValueAsString(dto)
                    )
            );
        } catch (IOException e) {
            logger.warn("Error to send message to {}: {}", session.getId(), e.getMessage());
        }
    }
}
