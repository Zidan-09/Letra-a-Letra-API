package com.letraaletra.api.features.matchmaking.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.matchmaking.application.input.ExitMatchmakingQueueInput;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingMessages;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.ExitMatchmakingGameWsRequest;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.ExitMatchmakingResponse;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper.ExitMatchmakingMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
public class ExitMatchmakingHandler implements RoomRequestHandler<ExitMatchmakingGameWsRequest> {
    private final UseCase<ExitMatchmakingQueueInput, Void> useCase;
    private final GameNotifier gameNotifier;

    public ExitMatchmakingHandler(
            UseCase<ExitMatchmakingQueueInput, Void> useCase,
            GameNotifier gameNotifier
    ) {
        this.useCase = useCase;
        this.gameNotifier = gameNotifier;
    }


    @Override
    public void handle(ExitMatchmakingGameWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        ExitMatchmakingQueueInput input = ExitMatchmakingMapper.toInput(userId);

        useCase.execute(input);

        notifier(userId, new ExitMatchmakingResponse(MatchmakingMessages.USER_LEFT_QUEUE));
    }

    @Override
    public Class<ExitMatchmakingGameWsRequest> getType() {
        return ExitMatchmakingGameWsRequest.class;
    }

    private void notifier(UUID userId, ExitMatchmakingResponse dto) {
        gameNotifier.notifierOne(userId, dto);
    }
}
