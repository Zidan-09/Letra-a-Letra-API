package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.inventory.application.usecase.InventoryPersistence;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.inventory.domain.InventoryMovement;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.ConsumableItem;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import com.letraaletra.api.features.user.application.input.ChangeNicknameInput;
import com.letraaletra.api.features.user.application.output.ChangeNicknameOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.NicknameAlreadyInUseException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.util.List;
import java.util.UUID;

public class ChangeNicknameUseCase implements UseCase<ChangeNicknameInput, ChangeNicknameOutput> {
    private static final String SOURCE_DETAIL = "CHANGE_NICKNAME";

    private final UserRepository userRepository;
    private final ItemLookup itemLookup;
    private final InventoryRepository inventoryRepository;
    private final BusinessAuditRecorder auditRecorder;

    public ChangeNicknameUseCase(
            UserRepository userRepository,
            ItemLookup itemLookup,
            InventoryRepository inventoryRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        this.userRepository = userRepository;
        this.itemLookup = itemLookup;
        this.inventoryRepository = inventoryRepository;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public ChangeNicknameOutput execute(ChangeNicknameInput input) {
        User user = userRepository.find(input.user())
                .orElseThrow(UserNotFoundException::new);

        validateNickname(input.nickname());

        Item item = itemLookup.getById(input.itemId());
        validateNicknameItem(item);

        Inventory inventory = Inventory.restore(
                input.user(),
                inventoryRepository.findItemsByOwner(input.user())
        );

        List<InventoryMovement> movements = inventory.consume(item, 1);

        user.setUsername(input.nickname());

        InventoryPersistence.save(inventoryRepository, input.user(), inventory);
        userRepository.save(user);

        UUID operationId = UUID.randomUUID();
        AuditEventFactory.itemChanges(
                movements,
                input.user(),
                new AuditActor(AuditActorType.USER, input.user(), null),
                null,
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                operationId
        ).forEach(auditRecorder::record);

        return new ChangeNicknameOutput(user);
    }

    private void validateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new NicknameAlreadyInUseException();
        }
    }

    private void validateNicknameItem(Item item) {
        if (!(item instanceof ConsumableItem consumable)
                || !(consumable.getEffect() instanceof NicknameChangeEffect)) {
            throw new InvalidItemException();
        }
    }
}
