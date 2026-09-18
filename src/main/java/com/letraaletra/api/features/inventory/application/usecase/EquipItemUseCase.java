package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.inventory.application.input.EquipItemInput;
import com.letraaletra.api.features.inventory.application.output.EquipItemOutput;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.inventory.domain.InventoryMovement;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class EquipItemUseCase implements UseCase<EquipItemInput, EquipItemOutput> {
    private static final String SOURCE_DETAIL = "EQUIP_ITEM";

    private final ItemLookup itemLookup;
    private final InventoryRepository inventoryRepository;
    private final BusinessAuditRecorder auditRecorder;

    public EquipItemUseCase(
            ItemLookup itemLookup,
            InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        this.itemLookup = itemLookup;
        this.inventoryRepository = inventoryRepository;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public EquipItemOutput execute(EquipItemInput input) {
        Item item = itemLookup.getById(input.itemId());

        Inventory inventory = Inventory.restore(
                input.userId(),
                inventoryRepository.findItemsByOwner(input.userId())
        );

        List<InventoryMovement> movements = inventory.equip(
                item,
                input.context(),
                itemLookup::getById
        );

        InventoryPersistence.save(inventoryRepository, input.userId(), inventory);

        AuditEventFactory.itemChanges(
                movements,
                input.userId(),
                new AuditActor(AuditActorType.USER, input.userId(), null),
                null,
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                java.util.UUID.randomUUID()
        ).forEach(auditRecorder::record);

        return new EquipItemOutput(movements);
    }
}
