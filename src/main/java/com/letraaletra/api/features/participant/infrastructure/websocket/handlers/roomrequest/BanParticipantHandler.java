package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.features.participant.application.usecase.BanParticipantUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.websocket.handlers.roomrequest.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.BanParticipantResponse;
import com.letraaletra.api.presentation.dto.response.websocket.ModerationResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.BanParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class BanParticipantHandler implements RoomRequestHandler<BanParticipantWsRequest> {
    @Autowired
    private BanParticipantUseCase banParticipant;

    @Autowired
    private BanParticipantMapper banParticipantMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(BanParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        BanParticipantInput command = banParticipantMapper.toCommand(request, userId);

        BanParticipantOutput output = banParticipant.execute(command);

        BanParticipantResponse dto = banParticipantMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);

        ModerationResponse dtoForBanned = new ModerationResponse("Banned from game");

        gameNotifier.notifierOne(request.participantId(), dtoForBanned);

    }

    @Override
    public Class<BanParticipantWsRequest> getType() {
        return BanParticipantWsRequest.class;
    }
}
