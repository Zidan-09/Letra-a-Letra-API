package com.letraaletra.api.features.audit.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import com.letraaletra.api.features.audit.domain.AuditEventDetails;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.projection.AuditEventProjection;

@DisplayName("AuditEventJpaMapper Unit Tests")
class AuditEventJpaMapperTest {

    @Test
    @DisplayName("round-trip domínio -> entidade -> domínio preserva todos os campos")
    void shouldRoundTripAllFields() {
        UUID eventId = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        UUID actorId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        AuditEvent event = new AuditEvent(
                eventId,
                occurredAt,
                AuditCategory.ECONOMY,
                AuditEventType.WALLET_DEBITED,
                AuditOutcome.SUCCESS,
                null,
                new AuditActor(AuditActorType.USER, actorId, "player"),
                targetUserId,
                AuditResourceType.WALLET,
                targetUserId.toString(),
                Map.of("SOFT", 100L),
                Map.of("SOFT", 50L),
                Map.of("SOFT", -50L),
                "SHOP_PURCHASE",
                "req-1",
                operationId,
                "game-1",
                AuditSourceType.HTTP,
                "POST /shop/offers/1/buy",
                transactionId,
                Map.of("k", "v")
        );

        var entity = AuditEventJpaMapper.toEntity(event);
        var restored = AuditEventJpaMapper.toDomain(entity);

        assertEquals(eventId, entity.getEventId());
        assertEquals(AuditCategory.ECONOMY.name(), entity.getCategory().name());

        assertEquals(restored.eventId(), event.eventId());
        assertEquals(restored.occurredAt(), event.occurredAt());
        assertEquals(restored.category(), event.category());
        assertEquals(restored.eventType(), event.eventType());
        assertEquals(restored.outcome(), event.outcome());
        assertEquals(restored.actor().type(), event.actor().type());
        assertEquals(restored.actor().id(), event.actor().id());
        assertEquals(restored.actor().name(), event.actor().name());
        assertEquals(restored.targetUserId(), event.targetUserId());
        assertEquals(restored.resourceType(), event.resourceType());
        assertEquals(restored.resourceId(), event.resourceId());
        assertEquals(restored.beforeState(), event.beforeState());
        assertEquals(restored.afterState(), event.afterState());
        assertEquals(restored.delta(), event.delta());
        assertEquals(restored.reasonCode(), event.reasonCode());
        assertEquals(restored.requestId(), event.requestId());
        assertEquals(restored.operationId(), event.operationId());
        assertEquals(restored.correlationId(), event.correlationId());
        assertEquals(restored.sourceType(), event.sourceType());
        assertEquals(restored.sourceDetail(), event.sourceDetail());
        assertEquals(restored.transactionId(), event.transactionId());
        assertEquals(restored.metadata(), event.metadata());
    }

    @Test
    @DisplayName("projection -> details preserva campos e targetUsername enriquecido")
    void shouldMapProjectionToDetails() {
        UUID eventId = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        UUID actorId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        UUID operationId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        AuditEvent event = new AuditEvent(
                eventId, occurredAt, AuditCategory.ECONOMY, AuditEventType.WALLET_CREDITED, AuditOutcome.SUCCESS, null,
                new AuditActor(AuditActorType.ADMIN, actorId, "admin1"), targetUserId,
                AuditResourceType.WALLET, targetUserId.toString(),
                Map.of("SOFT", 10L), Map.of("SOFT", 20L), Map.of("SOFT", 10L),
                "ADMIN_GIVE", "req-xyz", operationId, "corr-1",
                AuditSourceType.HTTP, "POST /admin/give", transactionId, Map.of("k", "v")
        );

        AuditEventProjection projection = AuditEventJpaMapper.toProjection(event, "playerOne");
        AuditEventDetails details = AuditEventJpaMapper.toDetails(projection);

        assertEquals(eventId, details.eventId());
        assertEquals(occurredAt, details.occurredAt());
        assertEquals(AuditCategory.ECONOMY, details.category());
        assertEquals(AuditEventType.WALLET_CREDITED, details.eventType());
        assertEquals(AuditActorType.ADMIN, details.actor().type());
        assertEquals(actorId, details.actor().id());
        assertEquals("admin1", details.actor().name());
        assertEquals(targetUserId, details.targetUserId());
        assertEquals("playerOne", details.targetUsername());
        assertEquals(AuditResourceType.WALLET, details.resourceType());
        assertEquals(Map.of("SOFT", 10L), details.beforeState());
        assertEquals("req-xyz", details.requestId());
        assertEquals(operationId, details.operationId());
    }

    @Test
    @DisplayName("projection null retorna details null")
    void shouldReturnNullForNullProjection() {
        assertNull(AuditEventJpaMapper.toDetails((AuditEventProjection) null));
        assertNull(AuditEventJpaMapper.toProjection(null));
        assertNull(AuditEventJpaMapper.toProjection(null, null));
    }

    @Test
    @DisplayName("projection com targetUsername null deve mapear para details com null")
    void shouldHandleNullTargetUsername() {
        AuditEvent event = AuditEvent.builder()
                .category(AuditCategory.ECONOMY)
                .eventType(AuditEventType.WALLET_CREDITED)
                .actor(new AuditActor(AuditActorType.SYSTEM, null, "SYSTEM"))
                .resourceType(AuditResourceType.WALLET)
                .resourceId("res-1")
                .sourceType(AuditSourceType.SYSTEM)
                .build();

        AuditEventProjection projection = AuditEventJpaMapper.toProjection(event, null);
        AuditEventDetails details = AuditEventJpaMapper.toDetails(projection);

        assertNull(details.targetUsername());
        assertNull(details.targetUserId());
        assertEquals(AuditActorType.SYSTEM, details.actor().type());
    }

    @Test
    @DisplayName("toDetails deve preservar JSONB maps null")
    void shouldPreserveNullMaps() {
        AuditEvent event = AuditEvent.builder()
                .category(AuditCategory.INVENTORY)
                .eventType(AuditEventType.COSMETIC_ACQUIRED)
                .actor(new AuditActor(AuditActorType.USER, UUID.randomUUID(), "player"))
                .resourceType(AuditResourceType.INVENTORY_ITEM)
                .resourceId(UUID.randomUUID().toString())
                .sourceType(AuditSourceType.HTTP)
                .build();

        AuditEventProjection projection = AuditEventJpaMapper.toProjection(event, "userX");
        AuditEventDetails details = AuditEventJpaMapper.toDetails(projection);

        assertNull(details.beforeState());
        assertNull(details.afterState());
        assertNull(details.delta());
        assertNull(details.metadata());
        assertEquals("userX", details.targetUsername());
    }
}
