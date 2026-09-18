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
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
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
            ItemLookup ItemLookup,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GetUserItemsUseCase(
                        inventoryRepository,
                        ItemLookup
                ),
                transactions
        );
    }

    @Bean
    public UseCase<GrantItemInput, GrantItemOutput> grantItemUseCase(
            ItemLookup ItemLookup,
            InventoryRepository inventoryRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new GrantItemUseCase(
                        ItemLookup,
                        inventoryRepository,
                        adminChecker,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public UseCase<ConsumeItemInput, ConsumeItemOutput> consumeItemUseCase(
            ItemLookup itemLookup,
            InventoryRepository inventoryRepository,
            UserRepository userRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new ConsumeItemUseCase(
                        itemLookup,
                        inventoryRepository,
                        userRepository,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public UseCase<EquipItemInput, EquipItemOutput> equipItemUseCase(
            ItemLookup ItemLookup,
            InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new EquipItemUseCase(
                        ItemLookup,
                        inventoryRepository,
                        auditRecorder
                ),
                transactions
        );
    }

    @Bean
    public UseCase<RevokeItemInput, RevokeItemOutput> revokeItemUseCase(
            ItemLookup ItemLookup,
            InventoryRepository inventoryRepository,
            AdminChecker adminChecker,
            BusinessAuditRecorder auditRecorder,
            TransactionalExecutorService transactions
    ) {
        return new TransactionalUseCase<>(
                new RevokeItemUseCase(
                        ItemLookup,
                        inventoryRepository,
                        adminChecker,
                        auditRecorder
                ),
                transactions
        );
    }
}
