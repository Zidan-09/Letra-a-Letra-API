package com.letraaletra.api.infra.websocket;

import com.letraaletra.api.dto.request.websocket.*;
import com.letraaletra.api.exception.exceptions.InvalidPlayerActionException;
import com.letraaletra.api.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WsRequestDispatcher {
    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerActionRequestDispatcher playerActionRequestDispatcher;

    public void dispatch(WsRequestDTO request, WebSocketSession session) {
        switch (request) {
            case CreateGameWsRequest create -> handleCreate(create, session);
            case JoinGameWsRequest join -> handleJoin(join, session);
            case StartGameWsRequest start -> handleStart(start, session);
            case PlayerActionWsRequest playerAction -> handlePlayerAction(playerAction, session);
            default -> throw new InvalidPlayerActionException();
        }
    }

    private void handleCreate(CreateGameWsRequest request, WebSocketSession session) {
        gameService.createGame(
                request.name(),
                request.gameSettings(),
                session.getId(),
                (String) session.getAttributes().get("userId")
        );
    }

    private void handleJoin(JoinGameWsRequest request, WebSocketSession session) {
        gameService.joinGame(
                request.tokenGameId(),
                session.getId()
        );
    }

    private void handleStart(StartGameWsRequest request, WebSocketSession session) {
        gameService.startGame(
                request.tokenGameId(),
                session.getId()
        );
    }

    private void handlePlayerAction(PlayerActionWsRequest request, WebSocketSession session) {
        playerActionRequestDispatcher.dispatch(request.tokenGameId(), request.action(), session);
    }
}
