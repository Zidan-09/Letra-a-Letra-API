package com.letraaletra.api.features.matchmaking.infrastructure.websocket.handlers;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.JoinMatchmakingGameWsRequest;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.JoinMatchmakingResponse;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper.JoinMatchmakingMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.WsMessageSender;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JoinMatchmakingHandler implements RoomRequestHandler<JoinMatchmakingGameWsRequest> {
    private final UseCase<JoinMatchmakingInput, Void> useCase;
    private final WsMessageSender messageSender;

    @Override
    public void handle(JoinMatchmakingGameWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        JoinMatchmakingInput input = JoinMatchmakingMapper.toInput(userId, session.getId(), request.gameMode());

        useCase.execute(input);

        JoinMatchmakingResponse dto = JoinMatchmakingMapper.toResponse();

        messageSender.sendToUser(userId, dto);
    }

    @Override
    public Class<JoinMatchmakingGameWsRequest> getType() {
        return JoinMatchmakingGameWsRequest.class;
    }
}
