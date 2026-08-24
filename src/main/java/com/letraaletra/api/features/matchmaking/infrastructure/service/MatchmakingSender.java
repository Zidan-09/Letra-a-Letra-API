package com.letraaletra.api.features.matchmaking.infrastructure.service;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.matchmaking.application.port.MatchmakingSenderService;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.response.MatchSuccessResponse;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.mapper.MatchSuccessMapper;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankSuccessResponse;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingSuccessMapper;
import com.letraaletra.api.features.queue.domain.QueueType;
import com.letraaletra.api.shared.infrastructure.websocket.WsMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchmakingSender implements MatchmakingSenderService {
    private final WsMessageSender messageSender;

    @Override
    public void notify(Game game, QueueType type) {
        List<String> socketIds = game.getParticipants().getParticipants().stream()
                .map(Participant::getSocketId)
                .toList();

        switch (type) {
            case CASUAL -> {
                MatchSuccessResponse dto = MatchSuccessMapper.toResponse(game);

                messageSender.sendToSessions(socketIds, dto);
            }

            case RANKING -> {
                RankSuccessResponse dto = RankingSuccessMapper.toResponse(game);

                messageSender.sendToSessions(socketIds, dto);
            }
        }
    }
}
