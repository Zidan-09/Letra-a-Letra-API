package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.inventory.application.input.ConsumeItemInput;
import com.letraaletra.api.features.inventory.application.output.ConsumeItemOutput;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.inventory.domain.InventoryMovement;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class ConsumeItemUseCase implements UseCase<ConsumeItemInput, ConsumeItemOutput> {
    private static final String SOURCE_DETAIL = "CONSUME_ITEM";

    private final ItemDefinitionRepository itemDefinitionRepository;
    private final InventoryRepository inventoryRepository;
    private final BusinessAuditRecorder auditRecorder;

    public ConsumeItemUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.inventoryRepository = inventoryRepository;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public ConsumeItemOutput execute(ConsumeItemInput input) {
        if (input.quantity() == null) {
            throw new InvalidQuantityException();
        }

        ItemDefinition definition = itemDefinitionRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        Inventory inventory = Inventory.restore(
                input.userId(),
                inventoryRepository.findItemsByOwner(input.userId())
        );

        List<InventoryMovement> movements = inventory.consume(definition, input.quantity(), input.context());

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

        return new ConsumeItemOutput(movements);
    }
}
