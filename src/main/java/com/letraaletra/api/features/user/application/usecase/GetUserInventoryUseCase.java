package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.repository.inventory.InventoryRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.data.domain.Page;

public class GetUserInventoryUseCase implements UseCase<GetUserInventoryInput, GetUserInventoryOutput> {
    private final InventoryRepository inventoryRepository;
    private final AdminChecker adminChecker;

    public GetUserInventoryUseCase(
            InventoryRepository inventoryRepository,
            AdminChecker adminChecker

    ) {
        this.inventoryRepository = inventoryRepository;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetUserInventoryOutput execute(GetUserInventoryInput input) {
        adminChecker.check(input.principal(), PermissionKey.USER, PermissionAction.VIEW);

        Page<InventoryItem> inventoryItems = inventoryRepository.getUsersCosmeticsPage(input.userId(), UsersPage.create(
                input.page(),
                input.size(),
                input.sort()
        ));

        return new GetUserInventoryOutput(inventoryItems);
    }
}
