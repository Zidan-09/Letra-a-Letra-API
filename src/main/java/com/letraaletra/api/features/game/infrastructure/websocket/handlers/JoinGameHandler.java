package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.JoinGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.JoinGameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.JoinGameMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class JoinGameHandler implements RoomRequestHandler<JoinGameWsRequest> {
    private final UseCase<JoinGameInput, JoinGameOutput> useCase;
    private final GameNotifier gameNotifier;

    @Transactional
    @Override
    public void handle(JoinGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        JoinGameInput input = JoinGameMapper.toInput(request, session.getId(), userId);

        JoinGameOutput output = useCase.execute(input);

        JoinGameResponse dto = JoinGameMapper.toResponse(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<JoinGameWsRequest> getType() {
        return JoinGameWsRequest.class;
    }
}

