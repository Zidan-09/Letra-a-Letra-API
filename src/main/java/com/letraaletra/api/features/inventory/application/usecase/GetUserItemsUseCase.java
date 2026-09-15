package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.inventory.application.output.UserItemDetails;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionLookup;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetUserItemsUseCase implements UseCase<GetUserItemsInput, GetUserItemsOutput> {
    private final InventoryRepository inventoryRepository;
    private final ItemDefinitionLookup itemLookup;

    public GetUserItemsUseCase(
            InventoryRepository inventoryRepository,
            ItemDefinitionLookup itemLookup
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

    private UserItemDetails toDetails(UserItem item) {
        ItemDefinition definition = itemLookup.getById(item.getDefinitionId());

        return new UserItemDetails(item, definition);
    }

    private boolean matches(UserItemDetails details, GetUserItemsInput input) {
        if (input.kind() != null && details.definition().getKind() != input.kind()) {
            return false;
        }

        if (input.category() != null && details.definition().getCategory() != input.category()) {
            return false;
        }

        if (input.context() != null && !details.definition().isApplicableTo(input.context())) {
            return false;
        }

        if (input.equipped() != null && details.item().isEquipped() != input.equipped()) {
            return false;
        }

        return true;
    }
}
