package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.exception.ItemAlreadyExistsException;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class UpdateItemDefinitionUseCase implements UseCase<UpdateItemDefinitionInput, UpdateItemDefinitionOutput> {
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final ItemAssetStorage assetStorage;
    private final ItemImageConverter imageConverter;
    private final AdminChecker adminChecker;

    public UpdateItemDefinitionUseCase(
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
    public UpdateItemDefinitionOutput execute(UpdateItemDefinitionInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.EDIT);

        ItemDefinition definition = itemDefinitionRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        if (input.name() != null) {
            itemDefinitionRepository.findByName(input.name())
                    .filter(found -> !found.getId().equals(definition.getId()))
                    .ifPresent(found -> {
                        throw new ItemAlreadyExistsException();
                    });
        }

        String oldAssetPath = definition.getAssetPath();
        String newAssetPath = null;
        boolean replaceOldAsset = false;

        if (input.isNewAsset() && definition.getKind() == ItemKind.COSMETIC) {
            if (input.asset() == null) {
                throw new InvalidItemException();
            }

            byte[] image = imageConverter.convertToWebp(input.asset().content(), input.asset().contentType());
            newAssetPath = assetStorage.upload(image, input.name() != null ? input.name() : definition.getName(), definition.getCategory());
            definition.setAssetPath(newAssetPath);
            replaceOldAsset = true;
        } else if (input.name() != null && !input.name().equals(definition.getName())
                && definition.getKind() == ItemKind.COSMETIC && oldAssetPath != null) {
            newAssetPath = assetStorage.copy(oldAssetPath, input.name(), definition.getCategory());
            definition.setAssetPath(newAssetPath);
            replaceOldAsset = true;
        }

        if (input.name() != null) {
            definition.setName(input.name());
        }

        if (input.available() != null) {
            definition.setAvailable(input.available());
        }

        definition.incrementVersion();

        try {
            itemDefinitionRepository.save(definition);
        } catch (Exception e) {
            if (replaceOldAsset) {
                assetStorage.delete(newAssetPath);
            }
            throw e;
        }

        if (replaceOldAsset) {
            assetStorage.delete(oldAssetPath);
        }

        return new UpdateItemDefinitionOutput(definition);
    }
}
