package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.command.participant.KickParticipantCommand;
import com.letraaletra.api.application.output.participant.KickParticipantOutput;
import com.letraaletra.api.application.usecase.participant.KickParticipantUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.KickParticipantResponseDTO;
import com.letraaletra.api.presentation.dto.response.websocket.ModerationResponseDTO;
import com.letraaletra.api.presentation.mappers.participant.KickParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

@Component
public class KickParticipantHandler implements RoomRequestHandler<KickParticipantWsRequest> {
    @Autowired
    private KickParticipantUseCase kickParticipant;

    @Autowired
    private KickParticipantMapper kickParticipantMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(KickParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        KickParticipantCommand command = kickParticipantMapper.toCommand(request, userId);

        KickParticipantOutput output = kickParticipant.execute(command);

        KickParticipantResponseDTO dto = kickParticipantMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);

        String json = objectMapper.writeValueAsString(
                new ModerationResponseDTO("Kicked from game")
        );

        gameNotifier.notifierOne(request.participantId(), json);
    }

    @Override
    public Class<KickParticipantWsRequest> getType() {
        return KickParticipantWsRequest.class;
    }
}
