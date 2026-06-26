package com.letraaletra.api.features.matchmaking.infrastructure.websocket;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.MatchSuccessResponse;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper.MatchSuccessMapper;
import org.springframework.stereotype.Component;

@Component
public class MatchmakingSender {
    private final GameNotifier notifier;

    public MatchmakingSender(
            GameNotifier notifier
    ) {
        this.notifier = notifier;
    }

    public void notifierPlayers(Game game) {
        MatchSuccessResponse dto = MatchSuccessMapper.toResponse(game);

        notifier.notifierAll(game, dto);
    }
}
