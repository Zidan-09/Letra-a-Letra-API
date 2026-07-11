package com.letraaletra.api.features.matchmaking.infrastructure.websocket.handlers;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.JoinMatchmakingGameWsRequest;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.JoinMatchmakingResponse;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper.JoinMatchmakingMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
public class JoinMatchmakingHandler implements RoomRequestHandler<JoinMatchmakingGameWsRequest> {
    private final UseCase<JoinMatchmakingInput, Void> useCase;
    private final GameNotifier gameNotifier;

    public JoinMatchmakingHandler(
            UseCase<JoinMatchmakingInput, Void> useCase,
            GameNotifier gameNotifier
    ) {
        this.useCase = useCase;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(JoinMatchmakingGameWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        JoinMatchmakingInput input = JoinMatchmakingMapper.toInput(userId, session.getId(), request.gameMode());

        useCase.execute(input);

        JoinMatchmakingResponse dto = JoinMatchmakingMapper.toResponse();

        notifier(userId, dto);
    }

    @Override
    public Class<JoinMatchmakingGameWsRequest> getType() {
        return JoinMatchmakingGameWsRequest.class;
    }

    private void notifier(UUID user, JoinMatchmakingResponse dto) {
        gameNotifier.notifierOne(user, dto);
    }
}
