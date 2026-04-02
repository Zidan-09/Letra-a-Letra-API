package com.letraaletra.api.infrastructure.websocket;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.domain.game.participant.Participant;
import com.letraaletra.api.domain.game.exception.GameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class BroadcastService implements GameNotifier {
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void send(Game game, Object dto) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        String json = objectMapper.writeValueAsString(dto);

        for (Participant participant : game.getParticipants()) {
            WebSocketSession session = sessionRepository.find(participant.getSocketId());

            if (session == null) {
                continue;
            }

            if (!session.isOpen()) {
                sessionRepository.remove(session);
                continue;
            }

            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                System.out.println("Error to send message to" + session.getId() + ": " + e.getMessage());
            }
        }
    }
}
