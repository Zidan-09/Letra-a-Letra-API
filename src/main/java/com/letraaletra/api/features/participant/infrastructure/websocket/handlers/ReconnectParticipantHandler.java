package com.letraaletra.api.features.participant.infrastructure.websocket.handlers;

import com.letraaletra.api.features.participant.application.input.ReconnectParticipantInput;
import com.letraaletra.api.features.participant.application.output.ReconnectParticipantOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.ReconnectParticipantResponse;
import com.letraaletra.api.features.participant.infrastructure.presentation.mapper.ReconnectParticipantMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class ReconnectParticipantHandler {
    private final UseCase<ReconnectParticipantInput, Optional<ReconnectParticipantOutput>> useCase;
    private final GameNotifier gameNotifier;

    public ReconnectParticipantHandler(
            UseCase<ReconnectParticipantInput, Optional<ReconnectParticipantOutput>> useCase,
            GameNotifier gameNotifier
    ) {
        this.useCase = useCase;
        this.gameNotifier = gameNotifier;
    }

    public void handle(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        ReconnectParticipantInput command = ReconnectParticipantMapper.toInput(userId, session.getId());

        Optional<ReconnectParticipantOutput> output = useCase.execute(command);

        output.ifPresent(out -> {
            ReconnectParticipantResponse dto = ReconnectParticipantMapper.toResponse(out);

            gameNotifier.notifierAll(out.game(), dto);
        });
    }
}
