package com.letraaletra.api.features.inventory.infrastructure.config;

import com.letraaletra.api.features.inventory.application.input.ConsumeItemInput;
import com.letraaletra.api.features.inventory.application.input.EquipItemInput;
import com.letraaletra.api.features.inventory.application.input.GetInventoryInput;
import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.input.GrantItemInput;
import com.letraaletra.api.features.inventory.application.input.RevokeItemInput;
import com.letraaletra.api.features.inventory.application.output.ConsumeItemOutput;
import com.letraaletra.api.features.inventory.application.output.EquipItemOutput;
import com.letraaletra.api.features.inventory.application.output.GetInventoryOutput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.inventory.application.output.GrantItemOutput;
import com.letraaletra.api.features.inventory.application.output.RevokeItemOutput;
import com.letraaletra.api.features.inventory.application.usecase.ConsumeItemUseCase;
import com.letraaletra.api.features.inventory.application.usecase.EquipItemUseCase;
import com.letraaletra.api.features.inventory.application.usecase.GetInventoryUseCase;
import com.letraaletra.api.features.inventory.application.usecase.GetUserItemsUseCase;
import com.letraaletra.api.features.inventory.application.usecase.GrantItemUseCase;
import com.letraaletra.api.features.inventory.application.usecase.RevokeItemUseCase;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionLookup;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import com.letraaletra.api.shared.application.usecase.TransactionalUseCase;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryConfig {
    @Bean
    public UseCase<GetInventoryInput, GetInventoryOutput> getInventoryUseCase(
            InventoryRepository inventoryRepository,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetInventoryUseCase(
                        inventoryRepository
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GetUserItemsInput, GetUserItemsOutput> getUserItemsUseCase(
            InventoryRepository inventoryRepository,
            ItemDefinitionLookup itemDefinitionLookup,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetUserItemsUseCase(
                        inventoryRepository,
                        itemDefinitionLookup
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GrantItemInput, GrantItemOutput> grantItemUseCase(
            ItemDefinitionLookup itemDefinitionLookup,
            InventoryRepository inventoryRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GrantItemUseCase(
                        itemDefinitionLookup,
                        inventoryRepository,
                        adminChecker,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ConsumeItemInput, ConsumeItemOutput> consumeItemUseCase(
            ItemDefinitionLookup itemDefinitionLookup,
            InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ConsumeItemUseCase(
                        itemDefinitionLookup,
                        inventoryRepository,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public UseCase<EquipItemInput, EquipItemOutput> equipItemUseCase(
            ItemDefinitionLookup itemDefinitionLookup,
            InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new EquipItemUseCase(
                        itemDefinitionLookup,
                        inventoryRepository,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public UseCase<RevokeItemInput, RevokeItemOutput> revokeItemUseCase(
            ItemDefinitionLookup itemDefinitionLookup,
            InventoryRepository inventoryRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RevokeItemUseCase(
                        itemDefinitionLookup,
                        inventoryRepository,
                        adminChecker,
                        auditRecorder
                ),
                transactions
        );
    }
}
