package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.features.game.application.usecase.JoinGameUseCase;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.JoinGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.JoinGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.JoinGameMapper;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class JoinGameHandler implements RoomRequestHandler<JoinGameWsRequest> {
    @Autowired
    private JoinGameUseCase joinGame;

    @Autowired
    private JoinGameMapper joinGameMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(JoinGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        JoinGameInput command = joinGameMapper.toCommand(request, session.getId(), userId);

        JoinGameOutput output = joinGame.execute(command);

        JoinGameResponse dto = joinGameMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<JoinGameWsRequest> getType() {
        return JoinGameWsRequest.class;
    }
}

