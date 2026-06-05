package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.application.usecase.LeftGameUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.LeftGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.LeftGameMapper;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class LeftGameHandler implements RoomRequestHandler<LeftGameWsRequest> {
    @Autowired
    private LeftGameUseCase leftGame;

    @Autowired
    private LeftGameMapper leftGameMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(LeftGameWsRequest request, WebSocketSession session) {
        LeftGameInput command = leftGameMapper.toCommand(request, session.getId());

        LeftGameOutput output = leftGame.execute(command);

        LeftGameResponse dto = leftGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);

        if (output.gameOverResult() != null) {
            gameNotifier.notifierGameOver(output.game(), output.gameOverResult());
        }
    }

    @Override
    public Class<LeftGameWsRequest> getType() {
        return LeftGameWsRequest.class;
    }
}
