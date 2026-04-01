package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.application.usecase.game.CreateGameUseCase;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.presentation.dto.request.websocket.CreateGameWsRequest;
import com.letraaletra.api.presentation.dto.request.websocket.RoomSettingsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class CreateGameHandler implements RoomRequestHandler<CreateGameWsRequest> {
    @Autowired
    private CreateGameUseCase createGame;

    @Override
    public void handle(CreateGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        RoomSettingsDTO settings = request.settings();

        createGame.execute(
                request.name(),
                new RoomSettings(
                        settings.allowSpectators(),
                        settings.privateGame()
                ),
                session.getId(),
                userId
        );
    }

    @Override
    public Class<CreateGameWsRequest> getType() {
        return CreateGameWsRequest.class;
    }
}
