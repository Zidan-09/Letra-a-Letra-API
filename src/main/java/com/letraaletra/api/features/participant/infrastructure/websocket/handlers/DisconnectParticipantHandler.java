package com.letraaletra.api.features.participant.infrastructure.websocket.handlers;

import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.participant.application.usecase.DisconnectUseCase;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.DisconnectParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.DisconnectParticipantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class DisconnectParticipantHandler {
    @Autowired
    private DisconnectParticipantMapper disconnectParticipantMapper;

    @Autowired
    private DisconnectUseCase disconnectUseCase;

    @Autowired
    private GameNotifier gameNotifier;

    public void handler(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        DisconnectParticipantInput command = disconnectParticipantMapper.toCommand(userId, session.getId());

        Optional<DisconnectParticipantOutput> output = disconnectUseCase.execute(command);

        output.ifPresent(out -> {
            DisconnectParticipantResponse dto = disconnectParticipantMapper.toResponseDTO(out);

            gameNotifier.notifierAll(out.game(), dto);
        });
    }
}
