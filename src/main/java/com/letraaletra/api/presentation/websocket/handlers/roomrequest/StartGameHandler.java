package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.game.usecase.StartGameUseCase;
import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.presentation.dto.request.websocket.GameSettingsDTO;
import com.letraaletra.api.presentation.dto.request.websocket.StartGameWsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class StartGameHandler implements RoomRequestHandler<StartGameWsRequest> {
    @Autowired
    private StartGameUseCase startGame;

    @Override
    public void handle(StartGameWsRequest request, WebSocketSession session) {
        GameSettingsDTO settingsDTO = request.settings();

        startGame.execute(
                request.tokenGameId(),
                new GameSettings(
                        settingsDTO.themeId(),
                        settingsDTO.gameMode()
                ),
                session.getId()
        );
    }

    @Override
    public Class<StartGameWsRequest> getType() {
        return StartGameWsRequest.class;
    }
}
