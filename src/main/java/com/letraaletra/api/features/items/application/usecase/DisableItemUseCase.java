package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.DisableItemInput;
import com.letraaletra.api.features.items.application.output.DisableItemOutput;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class DisableItemUseCase implements UseCase<DisableItemInput, DisableItemOutput> {
    private final AdminChecker adminChecker;
    private final ItemRepository itemRepository;

    public DisableItemUseCase(
            AdminChecker adminChecker,
            ItemRepository itemRepository
    ) {
        this.adminChecker = adminChecker;
        this.itemRepository = itemRepository;
    }

    @Override
    public DisableItemOutput execute(DisableItemInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.EDIT);

        Item item = itemRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        item.disable();

        itemRepository.save(item);

        return new DisableItemOutput(item);
    }
}
