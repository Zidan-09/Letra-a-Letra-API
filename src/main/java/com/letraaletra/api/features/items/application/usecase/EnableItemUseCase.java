package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.EnableItemInput;
import com.letraaletra.api.features.items.application.output.EnableItemOutput;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class EnableItemUseCase implements UseCase<EnableItemInput, EnableItemOutput> {
    private final AdminChecker adminChecker;
    private final ItemRepository itemRepository;

    public EnableItemUseCase(
            AdminChecker adminChecker,
            ItemRepository itemRepository
    ) {
        this.adminChecker = adminChecker;
        this.itemRepository = itemRepository;
    }

    @Override
    public EnableItemOutput execute(EnableItemInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.EDIT);

        Item item = itemRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        item.enable();

        itemRepository.save(item);

        return new EnableItemOutput(item);
    }
}
