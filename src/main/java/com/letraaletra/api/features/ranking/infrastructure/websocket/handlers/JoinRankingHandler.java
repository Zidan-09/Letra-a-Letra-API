package com.letraaletra.api.features.ranking.infrastructure.websocket.handlers;

import com.letraaletra.api.features.ranking.application.input.JoinRankingInput;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request.JoinRankingGameWsRequest;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.JoinRankingResponse;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.JoinRankingMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.WsMessageSender;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JoinRankingHandler implements RoomRequestHandler<JoinRankingGameWsRequest> {
    private final UseCase<JoinRankingInput, Void> useCase;
    private final WsMessageSender messageSender;

    @Override
    public void handle(JoinRankingGameWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        JoinRankingInput input = JoinRankingMapper.toInput(userId, session.getId());

        useCase.execute(input);

        JoinRankingResponse dto = JoinRankingMapper.toResponse();

        messageSender.sendToUser(userId, dto);
    }

    @Override
    public Class<JoinRankingGameWsRequest> getType() {
        return JoinRankingGameWsRequest.class;
    }
}
