package com.letraaletra.api.features.matchmaking.infrastructure.websocket.handlers;

import com.letraaletra.api.features.matchmaking.application.input.ExitMatchmakingQueueInput;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingMessages;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.ExitMatchmakingGameWsRequest;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.ExitMatchmakingResponse;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper.ExitMatchmakingMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.WsMessageSender;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExitMatchmakingHandler implements RoomRequestHandler<ExitMatchmakingGameWsRequest> {
    private final UseCase<ExitMatchmakingQueueInput, Void> useCase;
    private final WsMessageSender messageSender;

    @Override
    public void handle(ExitMatchmakingGameWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        ExitMatchmakingQueueInput input = ExitMatchmakingMapper.toInput(userId);

        useCase.execute(input);

        messageSender.sendToUser(userId, new ExitMatchmakingResponse(MatchmakingMessages.USER_LEFT_QUEUE));
    }

    @Override
    public Class<ExitMatchmakingGameWsRequest> getType() {
        return ExitMatchmakingGameWsRequest.class;
    }
}
