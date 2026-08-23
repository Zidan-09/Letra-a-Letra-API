package com.letraaletra.api.features.audit.infrastructure.persistence.postgres;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.adapter.JpaAuditEventRepository;
import com.letraaletra.api.shared.infrastructure.audit.MdcOperationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({JpaAuditEventRepository.class, MdcOperationContext.class})
@DisplayName("Audit Atomicity Integration Tests (H2)")
class AuditAtomicityIntegrationTest {

    @Autowired
    private JpaAuditEventRepository repository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private AuditEvent sampleEvent(String eventType) {
        return AuditEvent.builder()
                .eventType(eventType.equals("CREDITED") ? AuditEventType.WALLET_CREDITED : AuditEventType.COMMAND_FAILED)
                .category(AuditCategory.ECONOMY)
                .actor(new AuditActor(AuditActorType.USER, UUID.randomUUID(), "player"))
                .resourceType(AuditResourceType.WALLET)
                .resourceId(UUID.randomUUID().toString())
                .sourceType(AuditSourceType.HTTP)
                .build();
    }

    @Test
    @DisplayName("evento SUCCESS n??o sobrevive ao rollback do neg??cio; FAILURE em REQUIRES_NEW sobrevive")
    void successEventMustRollbackAndFailureEventMustSurvive() {
        TransactionTemplate requiresNew = new TransactionTemplate(transactionManager);
        requiresNew.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);

        assertThrows(IllegalStateException.class, () ->
                requiresNew.executeWithoutResult(status -> {
                    repository.save(sampleEvent("CREDITED"));

                    throw new IllegalStateException("business rollback");
                }));

        long committedAfterRollback = repository.find(
                new AuditEventFilter(null, null, AuditEventType.WALLET_CREDITED, null,
                        null, null, null, null, null, null, null, null, null),
                0, 50, true).getTotalElements();

        assertEquals(0, committedAfterRollback);

        var failureRecorder = new com.letraaletra.api.features.audit.infrastructure.service.PersistedAuditRecorder(
                repository,
                new MdcOperationContext(),
                transactionManager
        );

        failureRecorder.recordFailure(sampleEvent("FAILED"));

        Page<com.letraaletra.api.features.audit.domain.AuditEvent> failures = repository.find(
                new AuditEventFilter(null, null, AuditEventType.COMMAND_FAILED, null,
                        null, null, null, null, null, null, null, null, null),
                0, 50, true);

        assertEquals(1, failures.getTotalElements());
        assertEquals(AuditOutcome.FAILURE, failures.getContent().get(0).outcome());
    }
}
