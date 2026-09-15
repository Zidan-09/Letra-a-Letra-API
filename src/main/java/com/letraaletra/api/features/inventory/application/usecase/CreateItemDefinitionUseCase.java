package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.application.port.ItemImageConverter;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.exception.InvalidItemException;
import com.letraaletra.api.features.inventory.domain.exception.ItemAlreadyExistsException;
import com.letraaletra.api.features.inventory.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

import java.util.Set;

public class CreateItemDefinitionUseCase implements UseCase<CreateItemDefinitionInput, CreateItemDefinitionOutput> {
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final ItemAssetStorage assetStorage;
    private final ItemImageConverter imageConverter;
    private final AdminChecker adminChecker;

    public CreateItemDefinitionUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            ItemAssetStorage assetStorage,
            ItemImageConverter imageConverter,
            AdminChecker adminChecker
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.assetStorage = assetStorage;
        this.imageConverter = imageConverter;
        this.adminChecker = adminChecker;
    }

    @Override
    public CreateItemDefinitionOutput execute(CreateItemDefinitionInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.CREATE);

        if (itemDefinitionRepository.findByName(input.name()).isPresent()) {
            throw new ItemAlreadyExistsException();
        }

        String assetPath = null;

        if (input.kind() == ItemKind.COSMETIC) {
            if (input.asset() == null) {
                throw new InvalidItemException();
            }

            byte[] image = imageConverter.convertToWebp(input.asset().content(), input.asset().contentType());
            assetPath = assetStorage.upload(image, input.name(), input.category());
        }

        try {
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
                    assetPath
            );

            itemDefinitionRepository.save(definition);

            return new CreateItemDefinitionOutput(definition);
        } catch (Exception e) {
            if (assetPath != null) {
                assetStorage.delete(assetPath);
            }
            throw e;
        }
    }
}
