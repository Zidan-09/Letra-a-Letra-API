package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.UpdateItemInput;
import com.letraaletra.api.features.items.application.output.UpdateItemOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.EquippableItem;
import com.letraaletra.api.features.items.domain.Item;
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

        Item item = itemRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        if (input.name() != null) {
            itemRepository.findByName(input.name())
                    .filter(found -> !found.getId().equals(item.getId()))
                    .ifPresent(found -> {
                        throw new ItemAlreadyExistsException();
                    });
        }

        String oldAssetPath = item instanceof EquippableItem equippable ? equippable.getAssetPath() : null;
        String newAssetPath = null;
        boolean replaceOldAsset = false;

        if (input.isNewAsset() && item instanceof EquippableItem equippable) {
            if (input.asset() == null) {
                throw new InvalidItemException();
            }

            byte[] image = imageConverter.convertToWebp(input.asset().content(), input.asset().contentType());
            newAssetPath = assetStorage.upload(image, input.name() != null ? input.name() : item.getName(), equippable.getCategory());
            equippable.setAssetPath(newAssetPath);
            replaceOldAsset = true;
        } else if (input.name() != null && !input.name().equals(item.getName())
                && item instanceof EquippableItem equippable && oldAssetPath != null) {
            newAssetPath = assetStorage.copy(oldAssetPath, input.name(), equippable.getCategory());
            equippable.setAssetPath(newAssetPath);
            replaceOldAsset = true;
        }

        if (input.name() != null) {
            item.setName(input.name());
        }

        applyAvailability(item, input.available());

        item.incrementVersion();

        try {
            itemRepository.save(item);
        } catch (Exception e) {
            if (replaceOldAsset) {
                assetStorage.delete(newAssetPath);
            }
            throw e;
        }

        if (replaceOldAsset) {
            assetStorage.delete(oldAssetPath);
        }

        return new UpdateItemOutput(item);
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
