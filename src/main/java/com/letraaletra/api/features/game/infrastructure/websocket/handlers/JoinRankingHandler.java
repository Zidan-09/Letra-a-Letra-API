package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.ranking.application.input.JoinRankingInput;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request.JoinRankingGameWsRequest;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.JoinRankingResponse;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.JoinRankingMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
public class JoinRankingHandler implements RoomRequestHandler<JoinRankingGameWsRequest> {
    private final UseCase<JoinRankingInput, Void> useCase;
    private final GameNotifier gameNotifier;

    public JoinRankingHandler(
            UseCase<JoinRankingInput, Void> useCase,
            GameNotifier gameNotifier
    ) {
        this.useCase = useCase;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(JoinRankingGameWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        JoinRankingInput input = JoinRankingMapper.toInput(userId, session.getId());

        useCase.execute(input);

        JoinRankingResponse dto = JoinRankingMapper.toResponse();

        notifier(userId, dto);
    }

    @Override
    public Class<JoinRankingGameWsRequest> getType() {
        return JoinRankingGameWsRequest.class;
    }

    private void notifier(UUID userId, JoinRankingResponse dto) {
        gameNotifier.notifierOne(userId, dto);
    }
}
