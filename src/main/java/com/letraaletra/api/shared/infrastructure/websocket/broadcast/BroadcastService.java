package com.letraaletra.api.shared.infrastructure.websocket.broadcast;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.game.domain.exception.GameNotFoundException;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.GameOverResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameOverMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class BroadcastService implements GameNotifier {
    private final SessionRepository sessionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(BroadcastService.class);

    public BroadcastService(
            SessionRepository sessionRepository
    ) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void notifierAll(Game game, Object dto) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        final String json;
        try {
            json = objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            logger.warn("Error serializing broadcast message: {}", e.getMessage());
            return;
        }

        for (Participant participant : game.getParticipants()) {
            WebSocketSession session = sessionRepository.find(participant.getSocketId());

            if (session == null || !session.isOpen()) {
                continue;
            }

            send(session, json);
        }
    }

    @Override
    public void notifierOne(UUID userId, Object dto) {
        WebSocketSession session = sessionRepository.findByUserId(userId);

        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(dto);
            send(session, json);
        } catch (Exception e) {
            logger.warn("Error serializing message for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public void notifierGameOver(Game game, GameOverResult gameOverResult) {
        GameOverResponse dto = GameOverMapper.toResponse(gameOverResult, game);

        notifierAll(game, dto);
    }

    private void send(WebSocketSession session, String json) {
        if (session == null || !session.isOpen()) return;

        try {
            session.sendMessage(new TextMessage(json));

        } catch (IOException | IllegalStateException e) {
            logger.warn("Error sending message to {}: {}",
                    session.getId(),
                    e.getMessage()
            );
        }
    }
}
