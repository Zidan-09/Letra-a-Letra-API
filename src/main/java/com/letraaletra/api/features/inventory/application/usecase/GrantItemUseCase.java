package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.inventory.application.input.GrantItemInput;
import com.letraaletra.api.features.inventory.application.output.GrantItemOutput;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.inventory.domain.InventoryMovement;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

import java.util.List;

public class GrantItemUseCase implements UseCase<GrantItemInput, GrantItemOutput> {
    private static final String SOURCE_DETAIL = "GRANT_ITEM";

    private final ItemDefinitionRepository itemDefinitionRepository;
    private final InventoryRepository inventoryRepository;
    private final AdminChecker adminChecker;
    private final BusinessAuditRecorder auditRecorder;

    public GrantItemUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            InventoryRepository inventoryRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.inventoryRepository = inventoryRepository;
        this.adminChecker = adminChecker;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public GrantItemOutput execute(GrantItemInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.EDIT);

        if (input.quantity() == null) {
            throw new InvalidQuantityException();
        }

        ItemDefinition definition = itemDefinitionRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        Inventory inventory = Inventory.restore(
                input.userId(),
                inventoryRepository.findItemsByOwner(input.userId())
        );

        List<InventoryMovement> movements = inventory.grant(definition, input.quantity());

        InventoryPersistence.save(inventoryRepository, input.userId(), inventory);

        AuditEventFactory.itemChanges(
                movements,
                input.userId(),
                new AuditActor(AuditActorType.ADMIN, input.principal().auth(), input.principal().name()),
                "ADMIN_GIVE",
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                java.util.UUID.randomUUID()
        ).forEach(auditRecorder::record);

        return new GrantItemOutput(movements);
    }
}
