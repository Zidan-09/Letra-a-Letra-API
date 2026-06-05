package com.letraaletra.api.presentation.websocket.handlers.roomrequest;

import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.application.usecase.StartGameUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.presentation.dto.request.StartGameWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.StartGameResponse;
import com.letraaletra.api.presentation.mappers.game.StartGameMapper;
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
