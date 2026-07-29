package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.GetMyInventoryInput;
import com.letraaletra.api.features.user.application.output.GetMyInventoryOutput;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.repository.InventoryRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;

public class GetMyInventoryUseCase implements UseCase<GetMyInventoryInput, GetMyInventoryOutput> {
    private final InventoryRepository inventoryRepository;

    public GetMyInventoryUseCase(
            InventoryRepository inventoryRepository
    ) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public GetMyInventoryOutput execute(GetMyInventoryInput input) {
        List<InventoryItem> inventoryItems = inventoryRepository.getCosmetics(input.userId());

        return buildOutput(inventoryItems);
    }

    private GetMyInventoryOutput buildOutput(List<InventoryItem> inventory) {
        return new GetMyInventoryOutput(
             inventory
        );
    }
}
