package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.StartGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.StartGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.StartGameMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class StartGameHandler implements RoomRequestHandler<StartGameWsRequest> {
    private final UseCase<StartGameInput, StartGameOutput> useCase;
    private final GameNotifier gameNotifier;

    public StartGameHandler(
            UseCase<StartGameInput, StartGameOutput> useCase,
            GameNotifier gameNotifier
    ) {
        this.useCase = useCase;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(StartGameWsRequest request, WebSocketSession session) {
        StartGameInput command = StartGameMapper.toInput(request, session.getId());

        StartGameOutput output = useCase.execute(command);

        StartGameResponse dto = StartGameMapper.toResponse(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<StartGameWsRequest> getType() {
        return StartGameWsRequest.class;
    }
}
