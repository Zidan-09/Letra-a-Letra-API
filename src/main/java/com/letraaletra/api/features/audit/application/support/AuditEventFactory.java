package com.letraaletra.api.features.audit.application.support;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.user.domain.inventory.InventoryMovement;
import com.letraaletra.api.features.user.domain.wallet.WalletMovement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AuditEventFactory {

    private AuditEventFactory() {
    }

    public static AuditEvent walletMovement(
            WalletMovement movement,
            UUID targetUserId,
            AuditActor actor,
            String reasonCode,
            AuditSourceType sourceType,
            String sourceDetail,
            UUID operationId,
            UUID transactionId
    ) {
        String coinKey = movement.coinType().name();
        long signedAmount = movement.operation().equals(OperationType.CREDIT)
                ? movement.amount()
                : -movement.amount();

        return base(actor, targetUserId, reasonCode, sourceType, sourceDetail, operationId)
                .category(AuditCategory.ECONOMY)
                .eventType(signedAmount >= 0 ? AuditEventType.WALLET_CREDITED : AuditEventType.WALLET_DEBITED)
                .resourceType(AuditResourceType.WALLET)
                .resourceId(targetUserId.toString())
                .beforeState(Map.of(coinKey, movement.balanceBefore().getAmountFor(movement.coinType())))
                .afterState(Map.of(coinKey, movement.balanceAfter().getAmountFor(movement.coinType())))
                .delta(Map.of(coinKey, signedAmount))
                .transactionId(transactionId)
                .build();
    }

    public static List<AuditEvent> inventoryChanges(
            List<InventoryMovement> movements,
            UUID targetUserId,
            AuditActor actor,
            String reasonCode,
            AuditSourceType sourceType,
            String sourceDetail,
            UUID operationId
    ) {
        return movements.stream()
                .map(movement -> inventoryChange(
                        movement,
                        targetUserId,
                        actor,
                        reasonCode,
                        sourceType,
                        sourceDetail,
                        operationId
                ))
                .toList();
    }

    public static AuditEvent inventoryChange(
            InventoryMovement movement,
            UUID targetUserId,
            AuditActor actor,
            String reasonCode,
            AuditSourceType sourceType,
            String sourceDetail,
            UUID operationId
    ) {
        AuditEventType eventType = switch (movement.kind()) {
            case ACQUIRED -> AuditEventType.COSMETIC_ACQUIRED;
            case REMOVED -> AuditEventType.COSMETIC_REVOKED;
            case EQUIPPED -> AuditEventType.COSMETIC_EQUIPPED;
            case UNEQUIPPED -> AuditEventType.COSMETIC_UNEQUIPPED;
        };

        var builder = base(actor, targetUserId, reasonCode, sourceType, sourceDetail, operationId)
                .category(AuditCategory.INVENTORY)
                .eventType(eventType)
                .resourceType(AuditResourceType.INVENTORY_ITEM)
                .resourceId(movement.cosmeticId().toString());

        if (movement.equippedBefore() != null) {
            builder.beforeState(Map.of("equipped", movement.equippedBefore()));
        }

        builder.afterState(Map.of("equipped", movement.equippedAfter()));

        return builder.build();
    }

    public static AuditEvent ticketCreated(Ticket ticket, AuditActor actor, UUID operationId) {
        return base(actor, ticket.getUserId(), null, AuditSourceType.HTTP, null, operationId)
                .category(AuditCategory.ACCOUNT)
                .eventType(AuditEventType.TICKET_CREATED)
                .resourceType(AuditResourceType.TICKET)
                .resourceId(ticket.getTicketId().toString())
                .afterState(Map.of(
                        "category", ticket.getCategory().name(),
                        "status", ticket.getStatus().name()
                ))
                .build();
    }

    public static AuditEvent ticketResolved(TicketStatus previousStatus, Ticket resolved, AuditActor actor, UUID operationId) {
        var builder = base(actor, resolved.getUserId(), null, AuditSourceType.HTTP, null, operationId)
                .category(AuditCategory.ADMINISTRATION)
                .eventType(AuditEventType.TICKET_RESOLVED)
                .resourceType(AuditResourceType.TICKET)
                .resourceId(resolved.getTicketId().toString())
                .beforeState(Map.of("status", previousStatus.name()))
                .afterState(resolvedAfterState(resolved));

        if (resolved.getResolutionNote() != null) {
            builder.metadata(Map.of("resolutionNote", resolved.getResolutionNote()));
        }

        return builder.build();
    }

    private static Map<String, Object> resolvedAfterState(Ticket resolved) {
        Map<String, Object> state = new HashMap<>();
        state.put("status", resolved.getStatus().name());
        state.put("resolvedByAdminId", resolved.getResolvedByAdminId().toString());
        return state;
    }

    private static AuditEvent.Builder base(
            AuditActor actor,
            UUID targetUserId,
            String reasonCode,
            AuditSourceType sourceType,
            String sourceDetail,
            UUID operationId
    ) {
        return AuditEvent.builder()
                .actor(actor)
                .targetUserId(targetUserId)
                .reasonCode(reasonCode)
                .sourceType(sourceType)
                .sourceDetail(sourceDetail)
                .operationId(operationId);
    }
}
