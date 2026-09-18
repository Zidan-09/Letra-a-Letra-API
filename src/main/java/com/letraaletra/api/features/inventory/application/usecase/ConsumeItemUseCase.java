package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.audit.application.support.AuditEventFactory;
import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.inventory.application.input.ConsumeItemInput;
import com.letraaletra.api.features.inventory.application.output.ConsumeItemOutput;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.inventory.domain.InventoryMovement;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import com.letraaletra.api.features.user.domain.effect.UserEffect;
import com.letraaletra.api.features.user.domain.effect.effects.CoinBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.ExperienceBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankProtectionEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankingPointsBonusEffect;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.usecase.UseCase;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConsumeItemUseCase implements UseCase<ConsumeItemInput, ConsumeItemOutput> {
    private static final String SOURCE_DETAIL = "CONSUME_ITEM";

    private final ItemLookup itemLookup;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final BusinessAuditRecorder auditRecorder;

    public ConsumeItemUseCase(
            ItemLookup itemLookup,
            InventoryRepository inventoryRepository,
            UserRepository userRepository,
            BusinessAuditRecorder auditRecorder
    ) {
        this.itemLookup = itemLookup;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public ConsumeItemOutput execute(ConsumeItemInput input) {
        Item item = itemLookup.getById(input.itemId());

        Inventory inventory = Inventory.restore(
                input.userId(),
                inventoryRepository.findItemsByOwner(input.userId())
        );

        List<InventoryMovement> movements = inventory.consume(item, input.quantity(), EquippableContext.PROFILE);

        InventoryPersistence.save(inventoryRepository, input.userId(), inventory);

        grantEffect(input.userId(), item);

        AuditEventFactory.itemChanges(
                movements,
                input.userId(),
                new AuditActor(AuditActorType.USER, input.userId(), null),
                null,
                AuditSourceType.HTTP,
                SOURCE_DETAIL,
                java.util.UUID.randomUUID()
        ).forEach(auditRecorder::record);

        return new ConsumeItemOutput(movements);
    }

    private void grantEffect(UUID userId, Item item) {
        toActiveEffect(item).ifPresent(effect ->
                userRepository.find(userId).ifPresent(user -> {
                    user.getActiveEffects().add(effect);
                    userRepository.save(user);
                }));
    }

    private Optional<UserEffect> toActiveEffect(Item item) {
        if (!(item instanceof ConsumableItem consumable)
                || !(consumable.getEffect() instanceof PercentageTimedEffect timed)) {
            return Optional.empty();
        }

        return switch (timed.type()) {
            case XP_BOOST_PCT -> Optional.of(new ExperienceBonusEffect(
                    timed.magnitude(), Instant.now().plusSeconds(timed.durationMinutes() * 60L)));
            case RANKING_POINTS_SHIELD -> Optional.of(new RankProtectionEffect(
                    Math.max(1, timed.durationMinutes())));
            case RANKING_POINTS_BOOST_PCT -> Optional.of(new RankingPointsBonusEffect(
                    timed.magnitude(), Instant.now().plusSeconds(timed.durationMinutes() * 60L)));
            case COIN_BOOST_PCT -> Optional.of(new CoinBonusEffect(
                    timed.magnitude(), Instant.now().plusSeconds(timed.durationMinutes() * 60L)));
            case NICKNAME_CHANGE_GRANT -> Optional.empty();
        };
    }
}
