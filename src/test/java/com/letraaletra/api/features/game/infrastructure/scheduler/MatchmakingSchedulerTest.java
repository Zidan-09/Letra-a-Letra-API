package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameState;
import com.letraaletra.api.features.matchmaking.application.port.GameAssemblerService;
import com.letraaletra.api.features.matchmaking.application.port.MatchmakingSenderService;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.queue.application.port.QueuePairProvider;
import com.letraaletra.api.features.queue.domain.OnlineUser;
import com.letraaletra.api.features.queue.domain.QueueMatch;
import com.letraaletra.api.features.queue.domain.QueueType;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchmakingScheduler Audit Tests")
class MatchmakingSchedulerTest {

    @Mock
    private QueuePairProvider pairProvider;

    @Mock
    private GameAssemblerService assembler;

    @Mock
    private MatchmakingSenderService sender;

    @Mock
    private BusinessAuditRecorder auditRecorder;

    @Mock
    private OperationContext operationContext;

    private MatchmakingScheduler scheduler;

    @BeforeEach
    void setUp() {
        lenient().when(operationContext.currentOperationId()).thenReturn(Optional.empty());
        lenient().doAnswer(inv -> { ((Runnable) inv.getArgument(2)).run(); return null; }).when(operationContext).runAsOperation(any(UUID.class), any(), any(Runnable.class));
        scheduler = new MatchmakingScheduler(pairProvider, assembler, sender, auditRecorder, operationContext);
    }

    @Test
    @DisplayName("pareamento autom??tico deve registrar MATCHMAKING_PAIRED com ator SYSTEM e origem SCHEDULER")
    void shouldRecordMatchmakingPairedEvent() {
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID matchId = UUID.randomUUID();

        MatchmakingPair pair = new MatchmakingPair(
                new OnlineUser(firstUserId, "session-1"),
                new OnlineUser(secondUserId, "session-2")
        );

        Game game = mock(Game.class);
        GameState gameState = mock(GameState.class);

        when(game.getId()).thenReturn(gameId);
        when(game.getGameState()).thenReturn(gameState);
        when(gameState.getMatchId()).thenReturn(matchId);

        when(pairProvider.get()).thenReturn(Optional.of(new QueueMatch(pair, QueueType.RANKING)));
        when(assembler.create(pair, QueueType.RANKING)).thenReturn(game);

        scheduler.processQueue();

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditRecorder).record(captor.capture());

        AuditEvent event = captor.getValue();
        assertEquals(AuditEventType.MATCHMAKING_PAIRED, event.eventType());
        assertEquals(AuditActorType.SYSTEM, event.actor().type());
        assertEquals(AuditSourceType.SCHEDULER, event.sourceType());
        assertEquals(AuditResourceType.MATCH, event.resourceType());
        assertEquals(matchId.toString(), event.resourceId());
        assertEquals(gameId.toString(), event.correlationId());

        verify(sender).notify(game, QueueType.RANKING);
    }
}
