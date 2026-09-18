package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.inventory.application.output.UserItemDetails;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetUserItemsUseCase implements UseCase<GetUserItemsInput, GetUserItemsOutput> {
    private final InventoryRepository inventoryRepository;
    private final ItemLookup itemLookup;

    public GetUserItemsUseCase(
            InventoryRepository inventoryRepository,
            ItemLookup itemLookup
    ) {
        this.inventoryRepository = inventoryRepository;
        this.itemLookup = itemLookup;
    }

    @Override
    public GetUserItemsOutput execute(GetUserItemsInput input) {
        List<UserItemDetails> items = inventoryRepository.findItemsByOwner(input.userId()).stream()
                .map(this::toDetails)
                .filter(details -> matches(details, input))
                .toList();

        return new GetUserItemsOutput(items);
    }

    private UserItemDetails toDetails(UserItem owned) {
        Item item = itemLookup.getById(owned.getItemId());

        return new UserItemDetails(owned, item);
    }

    private boolean matches(UserItemDetails details, GetUserItemsInput input) {
        if (input.kind() != null && kindOf(details.item()) != input.kind()) {
            return false;
        }

        if (input.category() != null && categoryOf(details.item()) != input.category()) {
            return false;
        }

        if (input.context() != null && contextOf(details.item()) != input.context()) {
            return false;
        }

        if (input.equipped() != null && details.owned().isEquipped() != input.equipped()) {
            return false;
        }

        return true;
    }

    private ItemKind kindOf(Item item) {
        return item instanceof ConsumableItem ? ItemKind.CONSUMABLE : ItemKind.EQUIPPABLE;
    }

    private EquippableCategory categoryOf(Item item) {
        if (item instanceof EquippableItem equippable) {
            return equippable.getCategory();
        }

        return null;
    }

    private EquippableContext contextOf(Item item) {
        if (item instanceof EquippableItem equippable) {
            return equippable.getContext();
        }

        return null;
    }
}
