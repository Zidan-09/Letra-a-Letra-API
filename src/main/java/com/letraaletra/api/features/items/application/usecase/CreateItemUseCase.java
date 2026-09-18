package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.CreateItemInput;
import com.letraaletra.api.features.items.application.output.CreateItemOutput;
import com.letraaletra.api.features.items.application.port.ItemImageConverter;
import com.letraaletra.api.features.items.domain.ConsumableItem;
import com.letraaletra.api.features.items.domain.EquippableItem;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.exception.ItemAlreadyExistsException;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class CreateItemUseCase implements UseCase<CreateItemInput, CreateItemOutput> {
    private final ItemRepository itemRepository;
    private final ItemAssetStorage assetStorage;
    private final ItemImageConverter imageConverter;
    private final AdminChecker adminChecker;

    public CreateItemUseCase(
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
    public CreateItemOutput execute(CreateItemInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.CREATE);

        if (itemRepository.findByName(input.name()).isPresent()) {
            throw new ItemAlreadyExistsException();
        }

        if (input.kind() == null) {
            throw new InvalidItemException();
        }

        String assetPath = null;

        if (input.kind() == ItemKind.EQUIPPABLE) {
            if (input.asset() == null || input.effect() != null) {
                throw new InvalidItemException();
            }

            byte[] image = imageConverter.convertToWebp(input.asset().content(), input.asset().contentType());
            assetPath = assetStorage.upload(image, input.name(), input.category());
        }

        try {
            Item item = input.kind() == ItemKind.EQUIPPABLE
                    ? EquippableItem.create(input.name(), input.context(), input.category(), assetPath)
                    : ConsumableItem.create(input.name(), input.category(), input.context(), input.effect());

            itemRepository.save(item);

            return new CreateItemOutput(item);
        } catch (Exception e) {
            if (assetPath != null) {
                assetStorage.delete(assetPath);
            }
            throw e;
        }
    }
}
