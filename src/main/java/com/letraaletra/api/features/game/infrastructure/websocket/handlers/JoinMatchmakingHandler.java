package com.letraaletra.api.features.game.infrastructure.websocket.handlers;

import com.letraaletra.api.features.matchmaking.application.input.JoinMatchmakingInput;
import com.letraaletra.api.features.matchmaking.application.output.JoinMatchmakingOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.matchmaking.application.usecase.JoinMatchmakingQueueUseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.JoinMatchmakingGameWsRequest;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.JoinMatchmakingResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.JoinMatchmakingMapper;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Component
public class JoinMatchmakingHandler implements RoomRequestHandler<JoinMatchmakingGameWsRequest> {
    private final JoinMatchmakingQueueUseCase joinMatchmakingQueueUseCase;
    private final GameNotifier gameNotifier;

    public JoinMatchmakingHandler(
            JoinMatchmakingQueueUseCase joinMatchmakingQueueUseCase,
            GameNotifier gameNotifier
    ) {
        this.joinMatchmakingQueueUseCase = joinMatchmakingQueueUseCase;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(JoinMatchmakingGameWsRequest request, WebSocketSession session) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        JoinMatchmakingInput command = JoinMatchmakingMapper.toInput(userId, session.getId(), request.gameMode());

        JoinMatchmakingOutput output = joinMatchmakingQueueUseCase.execute(command);

        JoinMatchmakingResponse dto = JoinMatchmakingMapper.toResponse(output);

        notifier(output.game().orElse(null), userId, dto);
    }

    @Override
    public Class<JoinMatchmakingGameWsRequest> getType() {
        return JoinMatchmakingGameWsRequest.class;
    }

    private void notifier(Game game, UUID user, JoinMatchmakingResponse dto) {
        if (game == null) {
            gameNotifier.notifierOne(user, dto);

        } else {
            gameNotifier.notifierAll(game, dto);
        }
    }
}
