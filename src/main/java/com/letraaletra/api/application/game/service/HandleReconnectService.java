package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.mappers.game.GameStateResponseAssembler;
import com.letraaletra.api.presentation.dto.response.websocket.GameStateUpdatedWsResponse;
import com.letraaletra.api.presentation.websocket.PlayerGameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class HandleReconnectService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private DisconnectManager disconnectManager;

    @Autowired
    private GameStateResponseAssembler gameStateResponseAssembler;

    @Autowired
    private PlayerGameContextResolver playerGameContextResolver;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(HandleReconnectService.class);

    public void execute(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        PlayerGameContext ctx = playerGameContextResolver.resolve(userId);
        if (ctx == null) return;

        Game game = ctx.game();

        disconnectManager.cancel(userId, game.getId());

        game.reconnect(userId, session.getId());

        gameRepository.save(game);

        GameStateUpdatedWsResponse data = buildResponse(game, mapUsersFromGame(game));
        sendResponse(data, session);
    }

    private Map<String, User> mapUsersFromGame(Game game) {
        List<String> ids = game.getGameState().getPlayerIds();
        return userRepository.findMapByIds(ids);
    }

    private GameStateUpdatedWsResponse buildResponse(Game game, Map<String, User> users) {
        return new GameStateUpdatedWsResponse(
                gameStateResponseAssembler.get(game.getGameState(), users)
        );
    }

    private void sendResponse(GameStateUpdatedWsResponse response, WebSocketSession session) {
        try {
            String json = objectMapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(json));

        } catch (IOException e) {
            logger.warn("Falha ao enviar mensagem para sessão {}: {}", session.getId(), e.getMessage());
        }
    }
}
