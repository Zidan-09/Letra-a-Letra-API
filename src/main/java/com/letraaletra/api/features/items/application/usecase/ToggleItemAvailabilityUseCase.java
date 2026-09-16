package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ToggleItemAvailabilityInput;
import com.letraaletra.api.features.items.application.output.ToggleItemAvailabilityOutput;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class ToggleItemAvailabilityUseCase implements UseCase<ToggleItemAvailabilityInput, ToggleItemAvailabilityOutput> {
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final AdminChecker adminChecker;

    public ToggleItemAvailabilityUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            AdminChecker adminChecker
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public ToggleItemAvailabilityOutput execute(ToggleItemAvailabilityInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.EDIT);

        if (input.available() == null) {
            throw new InvalidItemException();
        }

        ItemDefinition definition = itemDefinitionRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        definition.setAvailable(input.available());

        itemDefinitionRepository.save(definition);

        return new ToggleItemAvailabilityOutput(definition);
    }
}
