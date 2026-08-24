package com.letraaletra.api.features.audit.infrastructure.persistence.postgres;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.adapter.JpaAuditEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaAuditEventRepository.class)
@DisplayName("JpaAuditEventRepository Integration Tests (H2)")
class JpaAuditEventRepositoryTest {

    @Autowired
    private JpaAuditEventRepository repository;

    @Test
    @DisplayName("deve salvar e recuperar evento com estados jsonb preservados")
    void shouldSaveAndRetrieveWithJsonStates() {
        UUID userId = UUID.randomUUID();

        AuditEvent event = sampleEvent(userId, AuditEventType.WALLET_DEBITED, Instant.now())
                .beforeState(Map.of("SOFT", 100L))
                .afterState(Map.of("SOFT", 50L))
                .delta(Map.of("SOFT", -50L))
                .build();

        repository.save(event);

        Page<AuditEvent> result = repository.find(
                new AuditEventFilter(userId, null, AuditEventType.WALLET_DEBITED, null,
                        null, null, null, null, null, null, null, null, null),
                0, 10, false);

        assertEquals(1, result.getTotalElements());

        AuditEvent loaded = result.getContent().get(0);
        assertEquals(100, ((Number) loaded.beforeState().get("SOFT")).intValue());
        assertEquals(50, ((Number) loaded.afterState().get("SOFT")).intValue());
        assertEquals(-50, ((Number) loaded.delta().get("SOFT")).intValue());
    }

    @Test
    @DisplayName("filtros combin??veis por usu??rio e tipo devem refinar o resultado")
    void shouldFilterByUserAndType() {
        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();

        repository.save(sampleEvent(userA, AuditEventType.WALLET_CREDITED, Instant.now()).build());
        repository.save(sampleEvent(userA, AuditEventType.COSMETIC_ACQUIRED, Instant.now()).build());
        repository.save(sampleEvent(userB, AuditEventType.WALLET_CREDITED, Instant.now()).build());

        AuditEventFilter filter = new AuditEventFilter(
                userA, null,
                AuditEventType.WALLET_CREDITED, null,
                null, null, null, null, null, null, null, null, null);

        Page<AuditEvent> result = repository.find(filter, 0, 10, true);

        assertEquals(1, result.getContent().size());
        assertEquals(userA.toString(), result.getContent().get(0).targetUserId().toString());
    }

    @Test
    @DisplayName("pagina????o e ordena????o por occurredAt devem funcionar")
    void shouldPaginateAndSort() {
        Instant base = Instant.now();
        UUID userId = UUID.randomUUID();

        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, base.minusSeconds(30)).build());
        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, base.minusSeconds(20)).build());
        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, base.minusSeconds(10)).build());

        Page<AuditEvent> desc = repository.find(AuditEventFilter.empty(), 0, 2, false);

        assertEquals(3, desc.getTotalElements());
        assertTrue(desc.getContent().size() <= 2);
        assertTrue(desc.getContent().get(0).occurredAt().isAfter(
                desc.getContent().get(desc.getContent().size() - 1).occurredAt()));

        Page<AuditEvent> ascPage0 = repository.find(AuditEventFilter.empty(), 0, 2, true);
        assertEquals(2, ascPage0.getContent().size());
        List<Instant> ascTimes = ascPage0.getContent().stream().map(AuditEvent::occurredAt).toList();
        assertTrue(ascTimes.get(0).isBefore(ascTimes.get(1)));

        Page<AuditEvent> ascPage1 = repository.find(AuditEventFilter.empty(), 1, 2, true);
        assertEquals(1, ascPage1.getContent().size());
    }

    @Test
    @DisplayName("existsByTransactionId deve responder corretamente")
    void shouldCheckExistenceByTransactionId() {
        UUID userId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        assertFalse(repository.existsByTransactionId(transactionId));

        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, Instant.now())
                .transactionId(transactionId)
                .build());

        assertTrue(repository.existsByTransactionId(transactionId));
    }

    private AuditEvent.Builder sampleEvent(UUID userId, AuditEventType type, Instant occurredAt) {
        return AuditEvent.builder()
                .category(type.name().startsWith("WALLET") ? AuditCategory.ECONOMY : AuditCategory.INVENTORY)
                .eventType(type)
                .actor(new AuditActor(AuditActorType.USER, UUID.randomUUID(), "player"))
                .targetUserId(userId)
                .resourceType(AuditResourceType.WALLET)
                .resourceId(userId.toString())
                .occurredAt(occurredAt)
                .sourceType(AuditSourceType.HTTP);
    }
}
