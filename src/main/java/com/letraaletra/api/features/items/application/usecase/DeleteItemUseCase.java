package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.DeleteItemInput;
import com.letraaletra.api.features.items.application.output.DeleteItemOutput;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemAssetStorage;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class DeleteItemUseCase implements UseCase<DeleteItemInput, DeleteItemOutput> {
    private final ItemRepository itemRepository;
    private final ItemAssetStorage assetStorage;
    private final AdminChecker adminChecker;

    public DeleteItemUseCase(
            ItemRepository itemRepository,
            ItemAssetStorage assetStorage,
            AdminChecker adminChecker
    ) {
        this.itemRepository = itemRepository;
        this.assetStorage = assetStorage;
        this.adminChecker = adminChecker;
    }

    @Override
    public DeleteItemOutput execute(DeleteItemInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.DELETE);

        Item item = itemRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        if (item instanceof EquippableItem equippable && equippable.getAssetPath() != null) {
            assetStorage.delete(equippable.getAssetPath());
        }

        itemRepository.delete(item);

        return new DeleteItemOutput(item);
    }
}
