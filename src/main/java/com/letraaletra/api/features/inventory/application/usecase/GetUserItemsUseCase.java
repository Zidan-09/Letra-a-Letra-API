package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.inventory.application.output.UserItemDetails;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetUserItemsUseCase implements UseCase<GetUserItemsInput, GetUserItemsOutput> {
    private final InventoryRepository inventoryRepository;
    private final ItemDefinitionRepository itemDefinitionRepository;

    public GetUserItemsUseCase(
            InventoryRepository inventoryRepository,
            ItemDefinitionRepository itemDefinitionRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.itemDefinitionRepository = itemDefinitionRepository;
    }

    @Override
    public GetUserItemsOutput execute(GetUserItemsInput input) {
        List<UserItemDetails> items = inventoryRepository.findItemsByOwner(input.userId()).stream()
                .map(item -> toDetails(item))
                .filter(details -> matches(details, input))
                .toList();

        return new GetUserItemsOutput(items);
    }

    private UserItemDetails toDetails(UserItem item) {
        ItemDefinition definition = itemDefinitionRepository.findById(item.getDefinitionId())
                .orElseThrow(ItemNotFoundException::new);

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
