package com.letraaletra.api.shared.infrastructure.service;

import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.ranking.domain.repository.RankingRepository;
import com.letraaletra.api.shared.application.port.QueuePairProvider;
import com.letraaletra.api.shared.domain.QueueMatch;
import com.letraaletra.api.shared.domain.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlternatingQueuePairProvider implements QueuePairProvider {
    private QueueType next = QueueType.CASUAL;

    private final MatchmakingRepository matchmakingRepository;
    private final RankingRepository rankingRepository;

    @Override
    public Optional<QueueMatch> get() {
        QueueType current = next;
        next = (next == QueueType.CASUAL)
                ? QueueType.RANKING
                : QueueType.CASUAL;

        Optional<QueueMatch> match = poll(current);

        return match.isPresent()
                ? match
                : poll(next);
    }

    private Optional<QueueMatch> poll(QueueType type) {
        return switch (type) {
            case CASUAL -> matchmakingRepository.pollPair();
            case RANKING -> rankingRepository.pollPair();
        };
    }
}
