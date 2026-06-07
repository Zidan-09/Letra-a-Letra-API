package com.letraaletra.api.features.participant.infrastructure.websocket.handlers;

import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.participant.application.usecase.ReconnectUseCase;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.ReconnectParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class ReconnectParticipantHandler {
    @Autowired
    private ReconnectUseCase reconnectUseCase;

    @Autowired
    private GameNotifier gameNotifier;

    public void handle(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        ReconnectParticipantInput command = ReconnectParticipantMapper.toInput(userId, session.getId());

        Optional<ReconnectParticipantOutput> output = reconnectUseCase.execute(command);

        output.ifPresent(out -> {
            ReconnectParticipantResponse dto = ReconnectParticipantMapper.toResponse(out);

            gameNotifier.notifierAll(out.game(), dto);
        });
    }
}
