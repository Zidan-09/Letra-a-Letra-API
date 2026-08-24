package com.letraaletra.api.features.ranking.infrastructure.websocket.handlers;

import com.letraaletra.api.features.ranking.application.input.ExitRankingQueueInput;
import com.letraaletra.api.features.ranking.domain.RankingMessages;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request.ExitRankingGameWsRequest;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.ExitRankingResponse;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.ExitRankingMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.WsMessageSender;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExitRankingHandler implements RoomRequestHandler<ExitRankingGameWsRequest> {
    private final UseCase<ExitRankingQueueInput, Void> useCase;
    private final WsMessageSender messageSender;

    @Override
    public void handle(ExitRankingGameWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        ExitRankingQueueInput input = ExitRankingMapper.toInput(userId);

        useCase.execute(input);

        messageSender.sendToUser(userId, new ExitRankingResponse(RankingMessages.USER_LEFT_QUEUE));
    }

    @Override
    public Class<ExitRankingGameWsRequest> getType() {
        return ExitRankingGameWsRequest.class;
    }
}
