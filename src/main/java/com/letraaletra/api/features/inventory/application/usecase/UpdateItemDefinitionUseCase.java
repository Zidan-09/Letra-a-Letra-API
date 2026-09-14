package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.exception.ItemAlreadyExistsException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class UpdateItemDefinitionUseCase implements UseCase<UpdateItemDefinitionInput, UpdateItemDefinitionOutput> {
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final AdminChecker adminChecker;

    public UpdateItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            AdminChecker adminChecker
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public UpdateItemDefinitionOutput execute(UpdateItemDefinitionInput input) {
        adminChecker.check(input.principal(), PermissionKey.COSMETIC, PermissionAction.EDIT);

        ItemDefinition definition = itemDefinitionRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        if (input.name() != null) {
            itemDefinitionRepository.findByName(input.name())
                    .filter(found -> !found.getId().equals(definition.getId()))
                    .ifPresent(found -> {
                        throw new ItemAlreadyExistsException();
                    });
            definition.setName(input.name());
        }

        if (input.assetPath() != null) {
            definition.setAssetPath(input.assetPath());
        }

        if (input.available() != null) {
            definition.setAvailable(input.available());
        }

        definition.incrementVersion();
        itemDefinitionRepository.save(definition);

        return new UpdateItemDefinitionOutput(definition);
    }
}
