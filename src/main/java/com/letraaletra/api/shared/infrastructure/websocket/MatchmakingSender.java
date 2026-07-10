package com.letraaletra.api.shared.infrastructure.websocket;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.MatchSuccessResponse;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper.MatchSuccessMapper;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankSuccessResponse;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingSuccessMapper;
import com.letraaletra.api.shared.domain.QueueType;
import org.springframework.stereotype.Component;

@Component
public class MatchmakingSender {
    private final GameNotifier notifier;

    public MatchmakingSender(
            GameNotifier notifier
    ) {
        this.notifier = notifier;
    }

    public void notifierPlayers(Game game, QueueType type) {
        switch (type) {
            case CASUAL -> {
                MatchSuccessResponse dto = MatchSuccessMapper.toResponse(game);

                notifier.notifierAll(game, dto);
            }

            case RANKING -> {
                RankSuccessResponse dto = RankingSuccessMapper.toResponse(game);

                notifier.notifierAll(game, dto);
            }
        }
    }
}
