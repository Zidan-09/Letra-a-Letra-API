package com.letraaletra.api.features.participant.infrastructure.websocket;

import com.letraaletra.api.features.participant.application.port.ParticipantNotifier;
import com.letraaletra.api.shared.infrastructure.websocket.WsMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WsParticipantNotifier implements ParticipantNotifier {
    private final WsMessageSender messageSender;

    @Override
    public void notifyAll(Collection<String> socketIds, Object dto) {
        messageSender.sendToSessions(socketIds, dto);
    }

    @Override
    public void notifyUser(UUID userId, Object dto) {
        messageSender.sendToUser(userId, dto);
    }
}
