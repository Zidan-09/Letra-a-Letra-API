package com.letraaletra.api.features.matchmaking.infrastructure.scheduler;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.matchmaking.application.port.GameAssemblerService;
import com.letraaletra.api.features.matchmaking.application.port.MatchmakingSenderService;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.queue.application.port.QueuePairProvider;
import com.letraaletra.api.features.queue.domain.QueueType;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MatchmakingScheduler {

    private static final String SOURCE_DETAIL = "MATCHMAKING_SCHEDULER";

    private final QueuePairProvider pairProvider;
    private final QueueRepository queueRepository;
    private final GameAssemblerService assembler;
    private final MatchmakingSenderService sender;
    private final BusinessAuditRecorder auditRecorder;
    private final OperationContext operationContext;

    private static final Logger logger = LoggerFactory.getLogger(MatchmakingScheduler.class);

    @Scheduled(fixedDelay = 10)
    public void processQueue() {
        try {
            pairProvider.get().ifPresent(queueMatch ->
                    startGame(queueMatch.pair(), queueMatch.type()));
        } catch (Exception e) {
            logger.error("Error processing matchmaking", e);
        }
    }

    private void startGame(MatchmakingPair pair, QueueType type) {
        operationContext.runAsOperation(UUID.randomUUID(), null, () -> {
            Game game;
            try {
                game = assembler.create(pair, type);
            } catch (Exception e) {
                logger.warn("Matchmaking failed, requeueing users: queueType={}, userIds=[{}, {}]",
                        type,
                        pair.first().userId(),
                        pair.second().userId(),
                        e
                );
                requeuePair(pair, type);
                return;
            }

            logger.info("Matchmaking game started: gameId={}, queueType={}",
                    game.getId(),
                    type
            );

            auditRecorder.record(AuditEvent.builder()
                    .category(AuditCategory.GAME)
                    .eventType(AuditEventType.MATCHMAKING_PAIRED)
                    .actor(AuditActor.system())
                    .resourceType(AuditResourceType.MATCH)
                    .resourceId(resolveMatchId(game))
                    .correlationId(game.getId().toString())
                    .sourceType(AuditSourceType.SCHEDULER)
                    .sourceDetail(SOURCE_DETAIL)
                    .metadata(Map.of(
                            "gameId", game.getId().toString(),
                            "queueType", type.name(),
                            "userIds", List.of(
                                    pair.first().userId().toString(),
                                    pair.second().userId().toString()
                            )
                    ))
                    .build());

            sender.notify(game, type);
        });
    }

    private void requeuePair(MatchmakingPair pair, QueueType type) {
        try {
            queueRepository.add(type, pair.first());
            queueRepository.add(type, pair.second());
        } catch (Exception e) {
            logger.error("Failed to requeue users after matchmaking failure: queueType={}, userIds=[{}, {}]",
                    type,
                    pair.first().userId(),
                    pair.second().userId(),
                    e
            );
        }
    }

    private String resolveMatchId(Game game) {
        if (game.getGameState() == null || game.getGameState().getMatchId() == null) {
            return game.getId().toString();
        }

        return game.getGameState().getMatchId().toString();
    }
}
