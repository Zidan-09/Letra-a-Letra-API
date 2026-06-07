package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.LeftGameInput;
import com.letraaletra.api.features.game.application.output.LeftGameOutput;
import com.letraaletra.api.features.game.application.usecase.LeftGameUseCase;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.LeftGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.LeftGameMapper;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class LeftGameHandler implements RoomRequestHandler<LeftGameWsRequest> {
    private final LeftGameUseCase leftGame;
    private final GameNotifier gameNotifier;

    public LeftGameHandler(
            LeftGameUseCase leftGame,
            GameNotifier gameNotifier
    ) {
        this.leftGame = leftGame;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(LeftGameWsRequest request, WebSocketSession session) {
        LeftGameInput command = LeftGameMapper.toInput(request, session.getId());

        LeftGameOutput output = leftGame.execute(command);

        LeftGameResponse dto = LeftGameMapper.toResponse(output);

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
