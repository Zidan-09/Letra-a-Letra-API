package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.GetItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.GetItemDefinitionOutput;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class GetItemDefinitionUseCase implements UseCase<GetItemDefinitionInput, GetItemDefinitionOutput> {
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final AdminChecker adminChecker;

    public GetItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            AdminChecker adminChecker
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetItemDefinitionOutput execute(GetItemDefinitionInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.VIEW);

        ItemDefinition definition = itemDefinitionRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        return new GetItemDefinitionOutput(definition);
    }
}
