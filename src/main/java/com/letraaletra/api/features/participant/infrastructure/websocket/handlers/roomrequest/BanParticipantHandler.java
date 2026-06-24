package com.letraaletra.api.features.participant.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.features.participant.application.usecase.BanParticipantUseCase;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.BanParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ModerationResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.BanParticipantMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
public class BanParticipantHandler implements RoomRequestHandler<BanParticipantWsRequest> {
    private final BanParticipantUseCase banParticipant;
    private final GameNotifier gameNotifier;

    public BanParticipantHandler(
            BanParticipantUseCase banParticipant,
            GameNotifier gameNotifier
    ) {
        this.banParticipant = banParticipant;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(BanParticipantWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        BanParticipantInput command = BanParticipantMapper.toInput(request, userId);

        BanParticipantOutput output = banParticipant.execute(command);

        BanParticipantResponse dto = BanParticipantMapper.toResponse(output);

        gameNotifier.notifierAll(output.game(), dto);

        ModerationResponse dtoForBanned = new ModerationResponse("Banned from game");

        gameNotifier.notifierOne(UUID.fromString(request.participantId()), dtoForBanned);

    }

    @Override
    public Class<BanParticipantWsRequest> getType() {
        return BanParticipantWsRequest.class;
    }
}
