package com.letraaletra.api.features.game.infrastructure.websocket;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.shared.infrastructure.websocket.WsMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WsGameNotifier implements GameNotifier {
    private final WsMessageSender messageSender;

    @Override
    public void notifierAll(Game game, Object dto) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        List<String> socketIds = game.getParticipants().getParticipants().stream()
                .map(Participant::getSocketId)
                .toList();

        messageSender.sendToSessions(socketIds, dto);
    }

    @Override
    public void notifierOne(UUID userId, Object dto) {
        messageSender.sendToUser(userId, dto);
    }

    @Override
    public void notifierGameOver(Game game, Object dto) {
        notifierAll(game, dto);
    }
}
