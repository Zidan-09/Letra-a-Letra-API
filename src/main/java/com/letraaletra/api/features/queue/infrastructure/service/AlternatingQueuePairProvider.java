package com.letraaletra.api.features.queue.infrastructure.service;

import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.queue.application.port.QueuePairProvider;
import com.letraaletra.api.features.queue.domain.QueueMatch;
import com.letraaletra.api.features.queue.domain.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlternatingQueuePairProvider implements QueuePairProvider {
    private QueueType next = QueueType.CASUAL;

    private final QueueRepository queueRepository;

    @Override
    public Optional<QueueMatch> get() {
        QueueType current = next;
        next = (next == QueueType.CASUAL)
                ? QueueType.RANKING
                : QueueType.CASUAL;

        Optional<QueueMatch> match = queueRepository.pollPair(current);

        return match.isPresent()
                ? match
                : queueRepository.pollPair(next);
    }
}
