package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.GetInventoryInput;
import com.letraaletra.api.features.inventory.application.output.GetInventoryOutput;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetInventoryUseCase implements UseCase<GetInventoryInput, GetInventoryOutput> {
    private final InventoryRepository inventoryRepository;

    public GetInventoryUseCase(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public GetInventoryOutput execute(GetInventoryInput input) {
        List<UserItem> items = inventoryRepository.findItemsByOwner(input.userId());

        return new GetInventoryOutput(items);
    }
}
