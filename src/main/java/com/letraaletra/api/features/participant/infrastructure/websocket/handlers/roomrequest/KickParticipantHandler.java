package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
import com.letraaletra.api.features.participant.application.usecase.KickParticipantUseCase;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.KickParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ModerationResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.KickParticipantMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
public class KickParticipantHandler implements RoomRequestHandler<KickParticipantWsRequest> {
    private final KickParticipantUseCase kickParticipant;
    private final GameNotifier gameNotifier;

    public KickParticipantHandler(
            KickParticipantUseCase kickParticipant,
            GameNotifier gameNotifier
    ) {
        this.kickParticipant = kickParticipant;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(KickParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        KickParticipantInput command = KickParticipantMapper.toInput(request, userId);

        KickParticipantOutput output = kickParticipant.execute(command);

        KickParticipantResponse dto = KickParticipantMapper.toResponse(output);

        gameNotifier.notifierAll(output.game(), dto);

        ModerationResponse dtoForKicked = new ModerationResponse("Kicked from game");

        gameNotifier.notifierOne(UUID.fromString(request.participantId()), dtoForKicked);
    }

    @Override
    public Class<KickParticipantWsRequest> getType() {
        return KickParticipantWsRequest.class;
    }
}
