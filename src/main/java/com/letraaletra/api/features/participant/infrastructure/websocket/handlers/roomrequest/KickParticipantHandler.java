package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
import com.letraaletra.api.features.participant.application.usecase.KickParticipantUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.websocket.handlers.roomrequest.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.KickParticipantResponse;
import com.letraaletra.api.presentation.dto.response.websocket.ModerationResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.KickParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class KickParticipantHandler implements RoomRequestHandler<KickParticipantWsRequest> {
    @Autowired
    private KickParticipantUseCase kickParticipant;

    @Autowired
    private KickParticipantMapper kickParticipantMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(KickParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        KickParticipantInput command = kickParticipantMapper.toCommand(request, userId);

        KickParticipantOutput output = kickParticipant.execute(command);

        KickParticipantResponse dto = kickParticipantMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);

        ModerationResponse dtoForKicked = new ModerationResponse("Kicked from game");

        gameNotifier.notifierOne(request.participantId(), dtoForKicked);
    }

    @Override
    public Class<KickParticipantWsRequest> getType() {
        return KickParticipantWsRequest.class;
    }
}
