package com.letraaletra.api.features.audit.application.support;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuditEventFactory Unit Tests")
class AuditEventFactoryTest {

    private final UUID targetUserId = UUID.randomUUID();
    private final UUID operationId = UUID.randomUUID();
    private final AuditActor actor = AuditActor.system();

    private com.letraaletra.api.features.inventory.domain.InventoryMovement itemMovement(
            com.letraaletra.api.features.inventory.domain.InventoryChangeKind kind
    ) {
        return new com.letraaletra.api.features.inventory.domain.InventoryMovement(
                UUID.randomUUID(), kind, false, true, 1, 1
        );
    }

    private AuditEvent singleItem(
            com.letraaletra.api.features.inventory.domain.InventoryChangeKind kind
    ) {
        List<AuditEvent> events = AuditEventFactory.itemChanges(
                List.of(itemMovement(kind)),
                targetUserId,
                actor,
                "TEST",
                AuditSourceType.HTTP,
                "TEST_DETAIL",
                operationId
        );

        assertEquals(1, events.size());
        return events.get(0);
    }

    @Test
    @DisplayName("movimentos novos devem mapear para eventos ITEM_*")
    void newMovementsShouldMapToItemEvents() {
        Map<com.letraaletra.api.features.inventory.domain.InventoryChangeKind, AuditEventType> expected = Map.of(
                com.letraaletra.api.features.inventory.domain.InventoryChangeKind.ACQUIRED, AuditEventType.ITEM_ACQUIRED,
                com.letraaletra.api.features.inventory.domain.InventoryChangeKind.REMOVED, AuditEventType.ITEM_REMOVED,
                com.letraaletra.api.features.inventory.domain.InventoryChangeKind.EQUIPPED, AuditEventType.ITEM_EQUIPPED,
                com.letraaletra.api.features.inventory.domain.InventoryChangeKind.UNEQUIPPED, AuditEventType.ITEM_UNEQUIPPED,
                com.letraaletra.api.features.inventory.domain.InventoryChangeKind.CONSUMED, AuditEventType.ITEM_CONSUMED,
                com.letraaletra.api.features.inventory.domain.InventoryChangeKind.QUANTITY_CHANGED, AuditEventType.ITEM_QUANTITY_CHANGED
        );

        expected.forEach((kind, eventType) ->
                assertEquals(eventType, singleItem(kind).eventType(),
                        kind + " deve mapear para " + eventType));
    }

    @Test
    @DisplayName("evento de item deve carregar resourceId e estados de equipped/quantity")
    void itemEventShouldCarryResourceAndStates() {
        UUID itemId = UUID.randomUUID();
        com.letraaletra.api.features.inventory.domain.InventoryMovement movement =
                new com.letraaletra.api.features.inventory.domain.InventoryMovement(
                        itemId,
                        com.letraaletra.api.features.inventory.domain.InventoryChangeKind.QUANTITY_CHANGED,
                        false, false, 3, 1
                );

        AuditEvent event = AuditEventFactory.itemChanges(
                List.of(movement), targetUserId, actor, "TEST",
                AuditSourceType.HTTP, "TEST_DETAIL", operationId).get(0);

        assertEquals(itemId.toString(), event.resourceId());
        assertEquals(Map.of("equipped", false, "quantity", 3), event.beforeState());
        assertEquals(Map.of("equipped", false, "quantity", 1), event.afterState());
    }
}
