package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotAvailableException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.policy.ItemPolicyRegistry;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.item.ItemKind;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.ItemEffect;
import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.catalog.ItemFilter;
import com.letraaletra.api.features.items.domain.catalog.ItemsPage;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Inventory {
    private final UUID ownerId;
    private final List<UserItem> items;
    private final ItemPolicyRegistry registry;

    private Inventory(UUID ownerId, List<UserItem> items, ItemPolicyRegistry registry) {
        this.ownerId = ownerId;
        this.items = items;
        this.registry = registry;
    }

    public static Inventory create(UUID ownerId) {
        return create(ownerId, ItemPolicyRegistry.defaults());
    }

    public static Inventory create(UUID ownerId, ItemPolicyRegistry registry) {
        if (ownerId == null) {
            throw new InvalidQuantityException();
        }

        return new Inventory(ownerId, new ArrayList<>(), registry);
    }

    public static Inventory restore(UUID ownerId, List<UserItem> items) {
        return restore(ownerId, items, ItemPolicyRegistry.defaults());
    }

    public static Inventory restore(UUID ownerId, List<UserItem> items, ItemPolicyRegistry registry) {
        return new Inventory(ownerId, new ArrayList<>(items), registry);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public List<UserItem> getItems() {
        return List.copyOf(items);
    }

    public List<UserItem> getEquipped(EquippableContext context, ItemLookup lookup) {
        List<UserItem> equipped = new ArrayList<>();

        for (UserItem item : items) {
            if (item.isEquipped()
                    && lookup.getById(item.getItemId()) instanceof EquippableItem equippable
                    && equippable.getContext() == context) {
                equipped.add(item);
            }
        }

        return List.copyOf(equipped);
    }

    public List<InventoryMovement> grant(Item item, int quantity) {
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException();
        }

        UserItem existing = findItem(item.getId()).orElse(null);

        registry.grantPolicyFor(item).checkGrant(item, existing, quantity);

        if (existing == null) {
            UserItem owned = UserItem.create(ownerId, item.getId(), quantity);
            items.add(owned);

            return List.of(new InventoryMovement(
                    item.getId(),
                    InventoryChangeKind.ACQUIRED,
                    null,
                    false,
                    0,
                    quantity
            ));
        }

        int before = existing.getQuantity();
        existing.increase(quantity);

        return List.of(new InventoryMovement(
                item.getId(),
                InventoryChangeKind.QUANTITY_CHANGED,
                existing.isEquipped(),
                existing.isEquipped(),
                before,
                existing.getQuantity()
        ));
    }

    public List<InventoryMovement> consume(Item item, int quantity) {
        return consume(item, quantity, EquippableContext.PROFILE);
    }

    public List<InventoryMovement> consume(Item item, int quantity, EquippableContext context) {
        UserItem owned = findItem(item.getId()).orElse(null);

        registry.consumePolicyFor(item).checkConsume(item, owned, quantity, context);

        int before = owned.getQuantity();
        owned.decrease(quantity);

        List<InventoryMovement> movements = new ArrayList<>();
        movements.add(new InventoryMovement(
                item.getId(),
                InventoryChangeKind.CONSUMED,
                false,
                false,
                before,
                owned.getQuantity()
        ));

        if (owned.getQuantity() == 0) {
            items.remove(owned);
            movements.add(new InventoryMovement(
                    item.getId(),
                    InventoryChangeKind.REMOVED,
                    false,
                    false,
                    0,
                    0
            ));
        } else {
            movements.add(new InventoryMovement(
                    item.getId(),
                    InventoryChangeKind.QUANTITY_CHANGED,
                    false,
                    false,
                    before,
                    owned.getQuantity()
            ));
        }

        return movements;
    }

    public List<InventoryMovement> equip(Item item, EquippableContext context, ItemLookup lookup) {
        UserItem owned = findItem(item.getId()).orElse(null);

        registry.equipPolicyFor(item).checkEquip(item, owned, context);

        List<InventoryMovement> movements = new ArrayList<>();

        for (UserItem ownedItem : items) {
            if (ownedItem == owned || !ownedItem.isEquipped()) {
                continue;
            }

            if (lookup.getById(ownedItem.getItemId()) instanceof EquippableItem other
                    && item instanceof EquippableItem equippable
                    && other.getCategory() == equippable.getCategory()
                    && other.getContext() == context) {
                ownedItem.markUnequipped();
                movements.add(new InventoryMovement(
                        ownedItem.getItemId(),
                        InventoryChangeKind.UNEQUIPPED,
                        true,
                        false,
                        ownedItem.getQuantity(),
                        ownedItem.getQuantity()
                ));
            }
        }

        if (!owned.isEquipped()) {
            owned.markEquipped();
            movements.add(new InventoryMovement(
                    item.getId(),
                    InventoryChangeKind.EQUIPPED,
                    false,
                    true,
                    owned.getQuantity(),
                    owned.getQuantity()
            ));
        }

        return movements;
    }

    public List<InventoryMovement> revoke(Item item, ItemLookup lookup) {
        UserItem owned = findItem(item.getId())
                .orElseThrow(ItemNotOwnedException::new);

        items.remove(owned);

        List<InventoryMovement> movements = new ArrayList<>();
        movements.add(new InventoryMovement(
                item.getId(),
                InventoryChangeKind.REMOVED,
                owned.isEquipped(),
                false,
                owned.getQuantity(),
                0
        ));

        if (owned.isEquipped()) {
            findFallback(item, lookup).ifPresent(fallback -> {
                fallback.markEquipped();
                movements.add(new InventoryMovement(
                        fallback.getItemId(),
                        InventoryChangeKind.EQUIPPED,
                        false,
                        true,
                        fallback.getQuantity(),
                        fallback.getQuantity()
                ));
            });
        }

        return movements;
    }

    private Optional<UserItem> findItem(UUID itemId) {
        return items.stream()
                .filter(owned -> owned.getItemId().equals(itemId))
                .findFirst();
    }

    private Optional<UserItem> findFallback(Item revoked, ItemLookup lookup) {
        return items.stream()
                .filter(candidate -> sameSlot(lookup.getById(candidate.getItemId()), revoked))
                .findFirst();
    }

    private boolean sameSlot(Item candidate, Item revoked) {
        if (candidate instanceof EquippableItem equippable && revoked instanceof EquippableItem target) {
            return equippable.getCategory() == target.getCategory()
                    && equippable.getContext() == target.getContext();
        }

        return false;
    }
}
