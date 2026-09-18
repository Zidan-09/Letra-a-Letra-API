package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ToggleItemAvailabilityInput;
import com.letraaletra.api.features.items.application.output.ToggleItemAvailabilityOutput;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class ToggleItemAvailabilityUseCase implements UseCase<ToggleItemAvailabilityInput, ToggleItemAvailabilityOutput> {
    private final ItemRepository itemRepository;
    private final AdminChecker adminChecker;

    public ToggleItemAvailabilityUseCase(
            ItemRepository itemRepository,
            AdminChecker adminChecker
    ) {
        this.itemRepository = itemRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public ToggleItemAvailabilityOutput execute(ToggleItemAvailabilityInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.EDIT);

        Item item = itemRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        if (input.available() && !item.isAvailable()) {
            item.enable();
        } else if (!input.available() && item.isAvailable()) {
            item.disable();
        }

        itemRepository.save(item);

        return new ToggleItemAvailabilityOutput(item);
    }
}
