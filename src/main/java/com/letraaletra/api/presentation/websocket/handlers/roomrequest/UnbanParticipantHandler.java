package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.participant.UnbanParticipantCommand;
import com.letraaletra.api.application.output.participant.UnbanParticipantOutput;
import com.letraaletra.api.application.usecase.participant.UnbanUserUseCase;
import com.letraaletra.api.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.UnbanParticipantResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.UnbanParticipantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class UnbanParticipantHandler implements RoomRequestHandler<UnbanParticipantWsRequest> {

    @Autowired
    private UnbanUserUseCase unbanUser;

    @Autowired
    private UnbanParticipantMapper unbanParticipantMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(UnbanParticipantHandler.class);

    @Override
    public void handle(UnbanParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        UnbanParticipantCommand command = unbanParticipantMapper.toCommand(request, userId);

        UnbanParticipantOutput output = unbanUser.execute(command);

        UnbanParticipantResponseDTO dto = unbanParticipantMapper.toResponseDTO(output);

        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(dto)));

        } catch (IOException e) {
            logger.warn("Error to send message to {}: {}", session.getId(), e.getMessage());
        }


    }

    @Override
    public Class<UnbanParticipantWsRequest> getType() {
        return UnbanParticipantWsRequest.class;
    }
}
