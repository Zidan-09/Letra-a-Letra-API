package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.exception.ItemAlreadyExistsException;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

import java.util.Set;

public class CreateItemDefinitionUseCase implements UseCase<CreateItemDefinitionInput, CreateItemDefinitionOutput> {
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final AdminChecker adminChecker;

    public CreateItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            AdminChecker adminChecker
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public CreateItemDefinitionOutput execute(CreateItemDefinitionInput input) {
        adminChecker.check(input.principal(), PermissionKey.COSMETIC, PermissionAction.CREATE);

        if (itemDefinitionRepository.findByName(input.name()).isPresent()) {
            throw new ItemAlreadyExistsException();
        }

        ItemDefinition definition = ItemDefinition.create(
                input.name(),
                input.kind(),
                input.category(),
                input.applicability() == null || input.applicability().isEmpty()
                        ? Set.of(ItemContext.PROFILE)
                        : input.applicability(),
                input.stackable(),
                input.maxStack(),
                input.consumable(),
                input.effect(),
                input.assetPath()
        );

        itemDefinitionRepository.save(definition);

        return new CreateItemDefinitionOutput(definition);
    }
}
