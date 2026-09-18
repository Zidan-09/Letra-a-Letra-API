package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.GetItemInput;
import com.letraaletra.api.features.items.application.output.GetItemOutput;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;

public class GetItemUseCase implements UseCase<GetItemInput, GetItemOutput> {
    private final ItemRepository itemRepository;
    private final AdminChecker adminChecker;

    public GetItemUseCase(
            ItemRepository itemRepository,
            AdminChecker adminChecker
    ) {
        this.itemRepository = itemRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetItemOutput execute(GetItemInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.VIEW);

        Item item = itemRepository.findById(input.itemId())
                .orElseThrow(ItemNotFoundException::new);

        return new GetItemOutput(item);
    }
}
