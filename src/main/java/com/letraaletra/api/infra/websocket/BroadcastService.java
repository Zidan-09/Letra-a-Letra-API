package com.letraaletra.api.infra.websocket;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.presentation.dto.response.websocket.WsResponseDTO;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class BroadcastService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void send(String gameId, WsResponseDTO dto) {
        Game game = gameRepository.find(gameId);

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
