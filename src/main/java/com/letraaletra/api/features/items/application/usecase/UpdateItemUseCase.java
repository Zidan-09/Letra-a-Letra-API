package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.UpdateItemInput;
import com.letraaletra.api.features.items.application.output.UpdateItemOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.exception.ItemAlreadyExistsException;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class UpdateItemUseCase implements UseCase<UpdateItemInput, UpdateItemOutput> {
    private final ItemRepository itemRepository;
    private final ItemAssetStorage assetStorage;
    private final ItemImageConverter imageConverter;
    private final AdminChecker adminChecker;

    public UpdateItemUseCase(
            ItemRepository itemRepository,
            ItemAssetStorage assetStorage,
            ItemImageConverter imageConverter,
            AdminChecker adminChecker
    ) {
        this.itemRepository = itemRepository;
        this.assetStorage = assetStorage;
        this.imageConverter = imageConverter;
        this.adminChecker = adminChecker;
    }

    @Override
    public UpdateItemOutput execute(UpdateItemInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.EDIT);

        Item current = itemRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        String name = input.name() != null ? input.name() : current.getName();

        if (input.name() != null) {
            itemRepository.findByName(input.name())
                    .filter(found -> !found.getId().equals(current.getId()))
                    .ifPresent(found -> {
                        throw new ItemAlreadyExistsException();
                    });
        }

        Item updated;
        String oldAssetPath = current instanceof EquippableItem equippable ? equippable.getAssetPath() : null;
        String newAssetPath = null;
        boolean replaceOldAsset = false;

        if (current instanceof EquippableItem equippable) {
            EquippableContext context = input.context() != null ? input.context() : equippable.getContext();
            EquippableCategory category =
                    input.category() != null ? input.category() : equippable.getCategory();

            if (input.effect() != null) {
                throw new InvalidItemException();
            }

            String assetPath = equippable.getAssetPath();

            if (input.isNewAsset()) {
                if (input.asset() == null) {
                    throw new InvalidItemException();
                }

                byte[] image = imageConverter.convertToWebp(input.asset().content(), input.asset().contentType());
                newAssetPath = assetStorage.upload(image, name, category);
                assetPath = newAssetPath;
                replaceOldAsset = true;
            } else if (!name.equals(current.getName()) && oldAssetPath != null) {
                newAssetPath = assetStorage.copy(oldAssetPath, name, category);
                assetPath = newAssetPath;
                replaceOldAsset = true;
            }

            updated = EquippableItem.restore(
                    current.getId(),
                    name,
                    current.getVersion(),
                    current.isAvailable(),
                    context,
                    category,
                    assetPath
            );
        } else if (current instanceof ConsumableItem consumable) {
            if (input.category() != null || input.context() != null || input.asset() != null || input.isNewAsset()) {
                throw new InvalidItemException();
            }

            updated = ConsumableItem.restore(
                    current.getId(),
                    name,
                    current.getVersion(),
                    current.isAvailable(),
                    input.effect() != null ? input.effect() : consumable.getEffect()
            );
        } else {
            throw new InvalidItemException();
        }

        if (current instanceof EquippableItem != updated instanceof EquippableItem) {
            throw new InvalidItemException();
        }

        applyAvailability(updated, input.available());

        updated.incrementVersion();

        try {
            itemRepository.save(updated);
        } catch (Exception e) {
            if (replaceOldAsset) {
                assetStorage.delete(newAssetPath);
            }
            throw e;
        }

        if (replaceOldAsset) {
            assetStorage.delete(oldAssetPath);
        }

        return new UpdateItemOutput(updated);
    }

    private void applyAvailability(Item item, Boolean available) {
        if (available == null) {
            return;
        }

        if (available && !item.isAvailable()) {
            item.enable();
        } else if (!available && item.isAvailable()) {
            item.disable();
        }
    }
}
