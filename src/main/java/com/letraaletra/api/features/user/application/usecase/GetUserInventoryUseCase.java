package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.repository.InventoryRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetUserInventoryUseCase implements UseCase<GetUserInventoryInput, GetUserInventoryOutput> {
    private final InventoryRepository inventoryRepository;

    public GetUserInventoryUseCase(
            InventoryRepository inventoryRepository
    ) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public GetUserInventoryOutput execute(GetUserInventoryInput input) {
        List<InventoryItem> inventoryItems = inventoryRepository.getCosmetics(input.userId());

        return buildOutput(inventoryItems);
    }

    private GetUserInventoryOutput buildOutput(List<InventoryItem> inventory) {
        return new GetUserInventoryOutput(
             inventory
        );
    }
}
