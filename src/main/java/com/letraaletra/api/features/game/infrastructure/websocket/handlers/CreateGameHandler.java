package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.features.game.application.usecase.CreateGameUseCase;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.CreateGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.CreateGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.CreateGameMapper;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class CreateGameHandler implements RoomRequestHandler<CreateGameWsRequest> {
    @Autowired
    private CreateGameMapper createGameMapper;

    @Autowired
    private CreateGameUseCase createGame;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(CreateGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        CreateGameInput command = createGameMapper.toCommand(request, session.getId(), userId);

        CreateGameOutput output = createGame.execute(command);

        CreateGameResponse dto = createGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<CreateGameWsRequest> getType() {
        return CreateGameWsRequest.class;
    }
}
