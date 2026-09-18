package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ListItemsInput;
import com.letraaletra.api.features.items.application.output.ListItemsOutput;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.catalog.ItemFilter;
import com.letraaletra.api.features.items.domain.catalog.ItemsPage;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import org.springframework.data.domain.Page;

public class ListItemsUseCase implements UseCase<ListItemsInput, ListItemsOutput> {
    private final ItemRepository itemRepository;
    private final AdminChecker adminChecker;

    public ListItemsUseCase(
            ItemRepository itemRepository,
            AdminChecker adminChecker
    ) {
        this.itemRepository = itemRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public ListItemsOutput execute(ListItemsInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.VIEW);

        Page<Item> items = itemRepository.findAll(
                new ItemFilter(
                        input.kind(),
                        input.category(),
                        input.available()
                ),
                new ItemsPage(
                        input.page(),
                        input.size(),
                        input.sort()
                )
        );

        return new ListItemsOutput(items);
    }
}
