package com.letraaletra.api.features.audit.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuditEvent Unit Tests")
class AuditEventTest {

    private AuditEvent.Builder validBuilder() {
        return AuditEvent.builder()
                .category(AuditCategory.ECONOMY)
                .eventType(AuditEventType.WALLET_CREDITED)
                .actor(new AuditActor(AuditActorType.USER, UUID.randomUUID(), "player"))
                .resourceType(AuditResourceType.WALLET)
                .resourceId(UUID.randomUUID().toString())
                .sourceType(AuditSourceType.HTTP);
    }

    @Nested
    @DisplayName("construção")
    class Construction {

        @Test
        @DisplayName("deve preencher defaults de eventId, occurredAt e outcome")
        void shouldFillDefaults() {
            AuditEvent event = validBuilder().build();

            assertNotNull(event.eventId());
            assertNotNull(event.occurredAt());
            assertEquals(AuditOutcome.SUCCESS, event.outcome());
            assertTrue(Math.abs(Instant.now().toEpochMilli() - event.occurredAt().toEpochMilli()) < 5_000);
        }

        @Test
        @DisplayName("deve aceitar valores explícitos incluindo estados e metadados")
        void shouldKeepExplicitValues() {
            Instant occurredAt = Instant.now().minusSeconds(60);
            Map<String, Object> before = Map.of("SOFT", 100L);
            Map<String, Object> after = Map.of("SOFT", 200L);
            Map<String, Object> delta = Map.of("SOFT", 100L);
            UUID transactionId = UUID.randomUUID();
            UUID operationId = UUID.randomUUID();

            AuditEvent event = validBuilder()
                    .occurredAt(occurredAt)
                    .outcome(AuditOutcome.FAILURE)
                    .failureReason("boom")
                    .beforeState(before)
                    .afterState(after)
                    .delta(delta)
                    .reasonCode("SHOP_PURCHASE")
                    .requestId("req-1")
                    .operationId(operationId)
                    .correlationId("game-1")
                    .transactionId(transactionId)
                    .metadata(Map.of("k", "v"))
                    .build();

            assertEquals(occurredAt, event.occurredAt());
            assertEquals(AuditOutcome.FAILURE, event.outcome());
            assertEquals(before, event.beforeState());
            assertEquals(after, event.afterState());
            assertEquals(delta, event.delta());
            assertEquals(transactionId, event.transactionId());
            assertEquals(operationId, event.operationId());
        }
    }

    @Nested
    @DisplayName("validação")
    class Validation {

        @Test
        @DisplayName("deve rejeitar evento sem categoria")
        void shouldRejectMissingCategory() {
            assertThrows(IllegalArgumentException.class,
                    () -> validBuilder().category(null).build());
        }

        @Test
        @DisplayName("deve rejeitar evento sem tipo")
        void shouldRejectMissingEventType() {
            assertThrows(IllegalArgumentException.class,
                    () -> validBuilder().eventType(null).build());
        }

        @Test
        @DisplayName("deve rejeitar evento sem ator")
        void shouldRejectMissingActor() {
            assertThrows(IllegalArgumentException.class,
                    () -> validBuilder().actor(null).build());
        }

        @Test
        @DisplayName("deve rejeitar evento sem recurso")
        void shouldRejectMissingResourceType() {
            assertThrows(IllegalArgumentException.class,
                    () -> validBuilder().resourceType(null).build());
        }

        @Test
        @DisplayName("deve rejeitar evento sem resourceId ou com resourceId em branco")
        void shouldRejectBlankResourceId() {
            assertThrows(IllegalArgumentException.class,
                    () -> validBuilder().resourceId("").build());
            assertThrows(IllegalArgumentException.class,
                    () -> validBuilder().resourceId(null).build());
        }

        @Test
        @DisplayName("deve truncar failureReason muito longo")
        void shouldTruncateLongFailureReason() {
            String huge = "x".repeat(900);

            AuditEvent event = validBuilder().failureReason(huge).build();

            assertEquals(500, event.failureReason().length());
        }
    }

    @Nested
    @DisplayName("imutabilidade")
    class Immutability {

        @Test
        @DisplayName("toBuilder não deve alterar o evento original")
        void toBuilderShouldNotMutateOriginal() {
            AuditEvent original = validBuilder().requestId("req-original").build();

            AuditEvent changed = original.toBuilder().requestId("req-other").build();

            assertEquals("req-original", original.requestId());
            assertEquals("req-other", changed.requestId());
        }

        @Test
        @DisplayName("mapas devem ser cópias imutáveis")
        void stateMapsShouldBeImmutable() {
            AuditEvent event = validBuilder()
                    .delta(new java.util.HashMap<>(Map.of("SOFT", 10L)))
                    .build();

            assertThrows(UnsupportedOperationException.class,
                    () -> event.delta().put("OTHER", 1L));
        }
    }
}
