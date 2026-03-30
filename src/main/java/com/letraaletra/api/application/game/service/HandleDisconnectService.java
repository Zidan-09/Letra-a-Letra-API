package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.presentation.websocket.PlayerGameContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class HandleDisconnectService {

    @Autowired
    private DisconnectManager disconnectManager;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerGameContextResolver playerGameContextResolver;

    public void execute(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        PlayerGameContext ctx = playerGameContextResolver.resolve(userId);
        if (ctx == null) return;

        disconnectManager.start(userId, ctx.game().getId());

        Participant participant = ctx.participant();

        participant.disconnect();

        gameRepository.save(ctx.game());
    }
}
