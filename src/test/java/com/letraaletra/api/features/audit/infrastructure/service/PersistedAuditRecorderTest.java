package com.letraaletra.api.features.audit.infrastructure.service;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.domain.repository.SaveAuditEvent;
import com.letraaletra.api.shared.application.port.OperationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersistedAuditRecorder Unit Tests")
class PersistedAuditRecorderTest {

    @Mock
    private SaveAuditEvent saveAuditEvent;

    @Mock
    private OperationContext operationContext;

    private PropagationCapturingTransactionManager transactionManager;

    private PersistedAuditRecorder recorder;

    @BeforeEach
    void setUp() {
        lenient().when(operationContext.currentRequestId()).thenReturn(Optional.empty());
        lenient().when(operationContext.currentOperationId()).thenReturn(Optional.empty());

        transactionManager = new PropagationCapturingTransactionManager();

        recorder = new PersistedAuditRecorder(saveAuditEvent, operationContext, transactionManager);
    }

    private AuditEvent.Builder baseBuilder() {
        return AuditEvent.builder()
                .category(AuditCategory.ECONOMY)
                .eventType(AuditEventType.WALLET_CREDITED)
                .actor(new AuditActor(AuditActorType.USER, UUID.randomUUID(), "player"))
                .resourceType(AuditResourceType.WALLET)
                .resourceId(UUID.randomUUID().toString())
                .sourceType(AuditSourceType.HTTP);
    }

    @Test
    @DisplayName("record deve enriquecer requestId/operationId do contexto e delegar à porta de escrita")
    void recordShouldEnrichFromContextAndDelegate() {
        String requestId = "req-123";
        UUID operationId = UUID.randomUUID();

        when(operationContext.currentRequestId()).thenReturn(Optional.of(requestId));
        when(operationContext.currentOperationId()).thenReturn(Optional.of(operationId));

        recorder.record(baseBuilder().build());

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(saveAuditEvent).save(captor.capture());

        AuditEvent saved = captor.getValue();
        assertEquals(requestId, saved.requestId());
        assertEquals(operationId, saved.operationId());
        assertEquals(AuditOutcome.SUCCESS, saved.outcome());
    }

    @Test
    @DisplayName("record deve preservar valores explícitos do evento sobre o contexto")
    void recordShouldPreferExplicitValues() {
        UUID explicitOperation = UUID.randomUUID();

        lenient().when(operationContext.currentRequestId()).thenReturn(Optional.of("context-req"));

        recorder.record(baseBuilder()
                .requestId("explicit-req")
                .operationId(explicitOperation)
                .build());

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(saveAuditEvent).save(captor.capture());

        assertEquals("explicit-req", captor.getValue().requestId());
        assertEquals(explicitOperation, captor.getValue().operationId());
    }

    @Test
    @DisplayName("record deve preencher sourceDetail a partir do MDC quando ausente")
    void recordShouldFillSourceDetailFromMdc() {
        org.slf4j.MDC.put(com.letraaletra.api.shared.infrastructure.audit.MdcOperationContext.SOURCE_DETAIL_KEY,
                "POST /shop/offers/1/buy");

        try {
            recorder.record(baseBuilder().build());
        } finally {
            org.slf4j.MDC.remove(com.letraaletra.api.shared.infrastructure.audit.MdcOperationContext.SOURCE_DETAIL_KEY);
        }

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(saveAuditEvent).save(captor.capture());

        assertEquals("POST /shop/offers/1/buy", captor.getValue().sourceDetail());
    }

    @Test
    @DisplayName("recordFailure deve forçar outcome FAILURE e abrir transação REQUIRES_NEW")
    void recordFailureShouldUseRequiresNewAndForceFailureOutcome() {
        recorder.recordFailure(baseBuilder().build());

        assertEquals(
                TransactionDefinition.PROPAGATION_REQUIRES_NEW,
                transactionManager.lastPropagation
        );

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(saveAuditEvent).save(captor.capture());

        assertEquals(AuditOutcome.FAILURE, captor.getValue().outcome());
    }

    @Test
    @DisplayName("recordFailure é best-effort: exceção de persistência não pode propagar")
    void recordFailureShouldSwallowPersistenceErrors() {
        doThrow(new RuntimeException("audit db down"))
                .when(saveAuditEvent)
                .save(any(AuditEvent.class));

        assertDoesNotThrow(() -> recorder.recordFailure(baseBuilder().build()));
    }

    @Test
    @DisplayName("record propaga falha de persistência (atomicidade com o negócio)")
    void recordShouldPropagatePersistenceErrors() {
        doThrow(new RuntimeException("db down"))
                .when(saveAuditEvent)
                .save(any(AuditEvent.class));

        assertThrows(RuntimeException.class, () -> recorder.record(baseBuilder().build()));
    }

    private static class PropagationCapturingTransactionManager implements PlatformTransactionManager {
        private Integer lastPropagation;

        @Override
        public TransactionStatus getTransaction(TransactionDefinition definition) {
            lastPropagation = definition.getPropagationBehavior();
            return new SimpleTransactionStatus();
        }

        @Override
        public void commit(TransactionStatus status) {
        }

        @Override
        public void rollback(TransactionStatus status) {
        }
    }
}
