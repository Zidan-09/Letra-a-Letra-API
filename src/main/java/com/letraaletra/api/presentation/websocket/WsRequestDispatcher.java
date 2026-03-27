package com.letraaletra.api.presentation.websocket;

import com.letraaletra.api.application.game.usecase.CreateGameUseCase;
import com.letraaletra.api.application.game.usecase.JoinGameUseCase;
import com.letraaletra.api.application.game.usecase.LeftGameUseCase;
import com.letraaletra.api.application.game.usecase.StartGameUseCase;
import com.letraaletra.api.presentation.dto.request.websocket.*;
import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.game.exceptions.InvalidPlayerActionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WsRequestDispatcher {
    @Autowired
    private CreateGameUseCase createGame;

    @Autowired
    private JoinGameUseCase joinGame;

    @Autowired
    private StartGameUseCase startGame;

    @Autowired
    private LeftGameUseCase leftGame;

    @Autowired
    private PlayerActionRequestDispatcher playerActionRequestDispatcher;

    public void dispatch(WsRequestDTO request, WebSocketSession session) {
        switch (request) {
            case CreateGameWsRequest create -> handleCreate(create, session);
            case JoinGameWsRequest join -> handleJoin(join, session);
            case StartGameWsRequest start -> handleStart(start, session);
            case LeftGameWsRequest left -> handleLeft(left, session);
            case PlayerActionWsRequest playerAction -> handlePlayerAction(playerAction, session);
            default -> throw new InvalidPlayerActionException();
        }
    }

    private void handleCreate(CreateGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        GameSettingsDTO settings = request.settings();

        createGame.execute(
                request.name(),
                new GameSettings(
                        userId,
                        settings.themeId(),
                        settings.gameMode(),
                        settings.allowSpectators(),
                        settings.privateGame()
                ),
                session.getId(),
                userId
        );
    }

    private void handleJoin(JoinGameWsRequest request, WebSocketSession session) {
        joinGame.execute(
                request.tokenGameId(),
                session.getId()
        );
    }

    private void handleStart(StartGameWsRequest request, WebSocketSession session) {
        startGame.execute(
                request.tokenGameId(),
                session.getId()
        );
    }

    private void handleLeft(LeftGameWsRequest request, WebSocketSession session) {
        leftGame.execute(
                request.tokenGameId(),
                session.getId()
        );
    }

    private void handlePlayerAction(PlayerActionWsRequest request, WebSocketSession session) {
        playerActionRequestDispatcher.dispatch(request.tokenGameId(), request.action(), session);
    }
}
