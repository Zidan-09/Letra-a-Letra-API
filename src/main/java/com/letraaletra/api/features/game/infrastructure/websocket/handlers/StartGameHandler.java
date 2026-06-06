package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.application.usecase.StartGameUseCase;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.StartGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.StartGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.StartGameMapper;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class StartGameHandler implements RoomRequestHandler<StartGameWsRequest> {
    @Autowired
    private StartGameUseCase startGame;

    @Autowired
    private StartGameMapper startGameMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(StartGameWsRequest request, WebSocketSession session) {
        StartGameInput command = startGameMapper.toCommand(request, session.getId());

        StartGameOutput output = startGame.execute(command);

        StartGameResponse dto = startGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<StartGameWsRequest> getType() {
        return StartGameWsRequest.class;
    }
}
