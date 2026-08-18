package com.letraaletra.api.shared.infrastructure.service;

import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.ranking.domain.repository.RankingRepository;
import com.letraaletra.api.shared.application.port.QueueChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckIfUserIsOnQueueService implements QueueChecker {
    private final MatchmakingRepository matchmakingRepository;
    private final RankingRepository rankingRepository;

    @Override
    public boolean checkQueues(UUID userId) {
        boolean isOnMatchmakingQueue = matchmakingRepository.onQueue(userId);

        boolean isOnRankingQueue = rankingRepository.onQueue(userId);

        return isOnMatchmakingQueue || isOnRankingQueue;
    }
}
