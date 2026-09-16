package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.exception.ItemAlreadyExistsException;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class CreateItemDefinitionUseCase implements UseCase<CreateItemDefinitionInput, CreateItemDefinitionOutput> {
    private static final int CONSUMABLE_MAX_STACK = 1000;

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

        if (input.context() == null) {
            throw new InvalidItemException();
        }

        String assetPath = null;

        if (input.kind() == ItemKind.COSMETIC) {
            if (input.asset() == null) {
                throw new InvalidItemException();
            }

            byte[] image = imageConverter.convertToWebp(input.asset().content(), input.asset().contentType());
            assetPath = assetStorage.upload(image, input.name(), input.category());
        }

        boolean stackable = input.kind() == ItemKind.CONSUMABLE;
        Integer maxStack = input.kind() == ItemKind.CONSUMABLE ? CONSUMABLE_MAX_STACK : null;

        try {
            ItemDefinition definition = ItemDefinition.create(
                    input.name(),
                    input.kind(),
                    input.category(),
                    input.context(),
                    stackable,
                    maxStack,
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
