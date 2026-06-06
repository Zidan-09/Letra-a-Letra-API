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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class JoinMatchmakingHandler implements RoomRequestHandler<JoinMatchmakingGameWsRequest> {
    @Autowired
    private JoinMatchmakingQueueUseCase joinMatchmakingQueueUseCase;

    @Autowired
    private JoinMatchmakingMapper joinMatchmakingMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(JoinMatchmakingGameWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        JoinMatchmakingInput command = joinMatchmakingMapper.toCommand(userId, session.getId(), request.gameMode());

        JoinMatchmakingOutput output = joinMatchmakingQueueUseCase.execute(command);

        JoinMatchmakingResponse dto = joinMatchmakingMapper.toResponseDTO(output);

        notifier(output.game().orElse(null), userId, dto);
    }

    @Override
    public Class<JoinMatchmakingGameWsRequest> getType() {
        return JoinMatchmakingGameWsRequest.class;
    }

    private void notifier(Game game, String user, JoinMatchmakingResponse dto) {
        if (game == null) {
            gameNotifier.notifierOne(user, dto);

        } else {
            gameNotifier.notifierAll(game, dto);
        }
    }
}
