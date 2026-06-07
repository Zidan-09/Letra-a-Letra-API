package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.features.game.application.usecase.CreateGameUseCase;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.CreateGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.CreateGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.CreateGameMapper;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class CreateGameHandler implements RoomRequestHandler<CreateGameWsRequest> {
    private final CreateGameUseCase createGame;
    private final GameNotifier gameNotifier;

    public CreateGameHandler(
            CreateGameUseCase createUserUseCase,
            GameNotifier gameNotifier
    ) {
        this.createGame = createUserUseCase;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(CreateGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        CreateGameInput command = CreateGameMapper.toInput(request, session.getId(), userId);

        CreateGameOutput output = createGame.execute(command);

        CreateGameResponse dto = CreateGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<CreateGameWsRequest> getType() {
        return CreateGameWsRequest.class;
    }
}
