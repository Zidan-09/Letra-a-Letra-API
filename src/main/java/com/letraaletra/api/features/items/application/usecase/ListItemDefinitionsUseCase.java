package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ListItemDefinitionsInput;
import com.letraaletra.api.features.items.application.output.ListItemDefinitionsOutput;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemDefinitionFilter;
import com.letraaletra.api.features.items.domain.ItemDefinitionsPage;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import org.springframework.data.domain.Page;

public class ListItemDefinitionsUseCase implements UseCase<ListItemDefinitionsInput, ListItemDefinitionsOutput> {
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final AdminChecker adminChecker;

    public ListItemDefinitionsUseCase(
            ItemDefinitionRepository itemDefinitionRepository,
            AdminChecker adminChecker
    ) {
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public ListItemDefinitionsOutput execute(ListItemDefinitionsInput input) {
        adminChecker.check(input.principal(), PermissionKey.ITEMS, PermissionAction.VIEW);

        Page<ItemDefinition> definitions = itemDefinitionRepository.findAll(
                new ItemDefinitionFilter(
                        input.kind(),
                        input.category(),
                        input.available()
                ),
                new ItemDefinitionsPage(
                        input.page(),
                        input.size(),
                        input.sort()
                )
        );

        return new ListItemDefinitionsOutput(definitions);
    }
}
