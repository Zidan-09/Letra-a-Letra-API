package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.DeleteItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.DeleteItemDefinitionOutput;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class DeleteItemDefinitionUseCase implements UseCase<DeleteItemDefinitionInput, DeleteItemDefinitionOutput> {
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final ItemAssetStorage assetStorage;
    private final AdminChecker adminChecker;

    public DeleteItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            ItemAssetStorage assetStorage,
            AdminChecker adminChecker
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.assetStorage = assetStorage;
        this.adminChecker = adminChecker;
    }

    @Override
    public DeleteItemDefinitionOutput execute(DeleteItemDefinitionInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.DELETE);

        ItemDefinition definition = itemDefinitionRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        if (definition.getKind() == ItemKind.COSMETIC && definition.getAssetPath() != null) {
            assetStorage.delete(definition.getAssetPath());
        }

        itemDefinitionRepository.delete(definition);

        return new DeleteItemDefinitionOutput(definition);
    }
}
