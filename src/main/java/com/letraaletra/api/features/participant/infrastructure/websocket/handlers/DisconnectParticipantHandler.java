package com.letraaletra.api.features.participant.infrastructure.websocket.handlers;

import com.letraaletra.api.features.participant.application.input.DisconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.DisconnectParticipantOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.DisconnectParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.DisconnectParticipantMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class DisconnectParticipantHandler {
    private final UseCase<DisconnectParticipantInput, Optional<DisconnectParticipantOutput>> useCase;
    private final GameNotifier gameNotifier;

    public DisconnectParticipantHandler(
            UseCase<DisconnectParticipantInput, Optional<DisconnectParticipantOutput>> useCase,
            GameNotifier gameNotifier
    ) {
        this.useCase = useCase;
        this.gameNotifier = gameNotifier;
    }

    public void handler(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        DisconnectParticipantInput command = DisconnectParticipantMapper.toInput(userId, session.getId());

        Optional<DisconnectParticipantOutput> output = useCase.execute(command);

        output.ifPresent(out -> {
            DisconnectParticipantResponse dto = DisconnectParticipantMapper.toResponse(out);

            gameNotifier.notifierAll(out.game(), dto);
        });
    }
}
