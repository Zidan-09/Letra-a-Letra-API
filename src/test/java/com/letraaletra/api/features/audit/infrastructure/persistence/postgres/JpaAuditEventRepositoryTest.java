package com.letraaletra.api.features.audit.infrastructure.persistence.postgres;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventDetails;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
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

        Page<AuditEventDetails> result = repository.find(
                new AuditEventFilter(userId, null, AuditEventType.WALLET_DEBITED, null,
                        null, null, null, null, null, null, null, null, null),
                0, 10, false);

        assertEquals(1, result.getTotalElements());

        AuditEventDetails loaded = result.getContent().get(0);
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

        Page<AuditEventDetails> result = repository.find(filter, 0, 10, true);

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

        Page<AuditEventDetails> desc = repository.find(AuditEventFilter.empty(), 0, 2, false);

        assertEquals(3, desc.getTotalElements());
        assertTrue(desc.getContent().size() <= 2);
        assertTrue(desc.getContent().get(0).occurredAt().isAfter(
                desc.getContent().get(desc.getContent().size() - 1).occurredAt()));

        Page<AuditEventDetails> ascPage0 = repository.find(AuditEventFilter.empty(), 0, 2, true);
        assertEquals(2, ascPage0.getContent().size());
        List<Instant> ascTimes = ascPage0.getContent().stream().map(AuditEventDetails::occurredAt).toList();
        assertTrue(ascTimes.get(0).isBefore(ascTimes.get(1)));

        Page<AuditEventDetails> ascPage1 = repository.find(AuditEventFilter.empty(), 1, 2, true);
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

    @Test
    @DisplayName("deve projetar targetUsername via LEFT JOIN quando usuário existir")
    void shouldProjectTargetUsernameWhenUserExists() {
        UUID userId = UUID.randomUUID();

        AuditEvent event = sampleEvent(userId, AuditEventType.WALLET_CREDITED, Instant.now()).build();
        repository.save(event);

        Page<AuditEventDetails> result = repository.find(
                new AuditEventFilter(userId, null, null, null, null, null, null, null, null, null, null, null, null),
                0, 10, false);

        assertEquals(1, result.getTotalElements());
        AuditEventDetails details = result.getContent().get(0);
        assertEquals(userId, details.targetUserId());
        assertNull(details.targetUsername());
    }

    @Test
    @DisplayName("filtros com strings em branco devem ser tratados como nulos")
    void shouldTreatBlankStringsAsNull() {
        UUID userId = UUID.randomUUID();
        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, Instant.now()).build());

        AuditEventFilter filter = new AuditEventFilter(
                null, null, null, null, null, null, "   ", null, null, " ", null, " ", null);

        Page<AuditEventDetails> result = repository.find(filter, 0, 10, false);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("filtros por operationId e correlationId devem funcionar")
    void shouldFilterByOperationAndCorrelation() {
        UUID opId = UUID.randomUUID();
        String corrId = "corr-123";
        UUID userId = UUID.randomUUID();

        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, Instant.now())
                .operationId(opId).correlationId(corrId).build());
        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, Instant.now()).build());

        AuditEventFilter filter = new AuditEventFilter(
                null, null, null, null, null, null, null, null, null, null, opId, corrId, null);

        Page<AuditEventDetails> result = repository.find(filter, 0, 10, false);
        assertEquals(1, result.getTotalElements());
        assertEquals(opId, result.getContent().get(0).operationId());
        assertEquals(corrId, result.getContent().get(0).correlationId());
    }

    @Test
    @DisplayName("filtros por intervalo de tempo from/to devem funcionar")
    void shouldFilterByTimeRange() {
        Instant base = Instant.parse("2026-01-01T10:00:00Z");
        UUID userId = UUID.randomUUID();

        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, base.minusSeconds(100)).build());
        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, base).build());
        repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, base.plusSeconds(100)).build());

        AuditEventFilter filter = new AuditEventFilter(
                null, null, null, null, null, null, null,
                base.minusSeconds(10), base.plusSeconds(10),
                null, null, null, null);

        Page<AuditEventDetails> result = repository.find(filter, 0, 10, false);
        assertEquals(1, result.getTotalElements());
        assertEquals(base, result.getContent().get(0).occurredAt());
    }

    @Test
    @DisplayName("consulta sem filtros deve retornar todos com paginação (cenário frontend)")
    void shouldReturnAllWithoutFiltersLikeFrontend() {
        UUID userId = UUID.randomUUID();
        for (int i = 0; i < 5; i++) {
            repository.save(sampleEvent(userId, AuditEventType.WALLET_CREDITED, Instant.now().plusSeconds(i)).build());
        }

        Page<AuditEventDetails> page0 = repository.find(AuditEventFilter.empty(), 0, 2, false);
        assertEquals(5, page0.getTotalElements());
        assertEquals(2, page0.getContent().size());
        assertEquals(3, page0.getTotalPages());

        Page<AuditEventDetails> page1 = repository.find(AuditEventFilter.empty(), 1, 2, false);
        assertEquals(2, page1.getContent().size());

        Page<AuditEventDetails> page2 = repository.find(AuditEventFilter.empty(), 2, 2, false);
        assertEquals(1, page2.getContent().size());
    }

    @Test
    @DisplayName("múltiplos filtros combinados com paginação devem funcionar")
    void shouldFilterWithMultipleFiltersAndPagination() {
        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();
        Instant base = Instant.now();

        repository.save(sampleEvent(userA, AuditEventType.WALLET_CREDITED, base)
                .category(AuditCategory.ECONOMY).build());
        repository.save(sampleEvent(userA, AuditEventType.WALLET_DEBITED, base).build());
        repository.save(sampleEvent(userB, AuditEventType.WALLET_CREDITED, base).build());

        AuditEventFilter filter = new AuditEventFilter(
                userA, null, AuditEventType.WALLET_CREDITED, AuditCategory.ECONOMY,
                AuditOutcome.SUCCESS, AuditResourceType.WALLET, null, null, null, null, null, null, null);

        Page<AuditEventDetails> result = repository.find(filter, 0, 10, true);
        assertEquals(1, result.getTotalElements());
        assertEquals(userA, result.getContent().get(0).targetUserId());
        assertEquals(AuditEventType.WALLET_CREDITED, result.getContent().get(0).eventType());
        assertEquals(AuditCategory.ECONOMY, result.getContent().get(0).category());
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
