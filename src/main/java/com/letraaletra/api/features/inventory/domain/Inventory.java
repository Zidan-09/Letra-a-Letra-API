package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotAvailableException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.policy.ItemPolicyRegistry;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionLookup;

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

    public List<UserItem> getEquipped(ItemContext context, ItemDefinitionLookup lookup) {
        List<UserItem> equipped = new ArrayList<>();

        for (UserItem item : items) {
            if (item.isEquipped() && lookup.getById(item.getDefinitionId()).isApplicableTo(context)) {
                equipped.add(item);
            }
        }

        return List.copyOf(equipped);
    }

    public List<InventoryMovement> grant(ItemDefinition definition, int quantity) {
        if (!definition.isAvailable()) {
            throw new ItemNotAvailableException();
        }

        UserItem existing = findItem(definition.getId()).orElse(null);

        registry.grantPolicyFor(definition.getKind()).checkGrant(definition, existing, quantity);

        if (existing == null) {
            UserItem item = UserItem.create(ownerId, definition.getId(), quantity);
            items.add(item);

            return List.of(new InventoryMovement(
                    definition.getId(),
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
                definition.getId(),
                InventoryChangeKind.QUANTITY_CHANGED,
                existing.isEquipped(),
                existing.isEquipped(),
                before,
                existing.getQuantity()
        ));
    }

    public List<InventoryMovement> consume(ItemDefinition definition, int quantity, ItemContext context) {
        UserItem owned = findItem(definition.getId()).orElse(null);

        registry.consumePolicyFor(definition.getKind()).checkConsume(definition, owned, quantity, context);

        int before = owned.getQuantity();
        owned.decrease(quantity);

        List<InventoryMovement> movements = new ArrayList<>();
        movements.add(new InventoryMovement(
                definition.getId(),
                InventoryChangeKind.CONSUMED,
                false,
                false,
                before,
                owned.getQuantity()
        ));

        if (owned.getQuantity() == 0) {
            items.remove(owned);
            movements.add(new InventoryMovement(
                    definition.getId(),
                    InventoryChangeKind.REMOVED,
                    false,
                    false,
                    0,
                    0
            ));
        } else {
            movements.add(new InventoryMovement(
                    definition.getId(),
                    InventoryChangeKind.QUANTITY_CHANGED,
                    false,
                    false,
                    before,
                    owned.getQuantity()
            ));
        }

        return movements;
    }

    public List<InventoryMovement> equip(ItemDefinition definition, ItemContext context, ItemDefinitionLookup lookup) {
        UserItem owned = findItem(definition.getId()).orElse(null);

        registry.equipPolicyFor(definition.getKind()).checkEquip(definition, owned, context);

        List<InventoryMovement> movements = new ArrayList<>();

        for (UserItem item : items) {
            if (item == owned || !item.isEquipped()) {
                continue;
            }

            ItemDefinition other = lookup.getById(item.getDefinitionId());

            if (other.getCategory() == definition.getCategory() && other.isApplicableTo(context)) {
                item.markUnequipped();
                movements.add(new InventoryMovement(
                        item.getDefinitionId(),
                        InventoryChangeKind.UNEQUIPPED,
                        true,
                        false,
                        item.getQuantity(),
                        item.getQuantity()
                ));
            }
        }

        if (!owned.isEquipped()) {
            owned.markEquipped();
            movements.add(new InventoryMovement(
                    definition.getId(),
                    InventoryChangeKind.EQUIPPED,
                    false,
                    true,
                    owned.getQuantity(),
                    owned.getQuantity()
            ));
        }

        return movements;
    }

    public List<InventoryMovement> revoke(ItemDefinition definition, ItemDefinitionLookup lookup) {
        UserItem owned = findItem(definition.getId())
                .orElseThrow(ItemNotOwnedException::new);

        items.remove(owned);

        List<InventoryMovement> movements = new ArrayList<>();
        movements.add(new InventoryMovement(
                definition.getId(),
                InventoryChangeKind.REMOVED,
                owned.isEquipped(),
                false,
                owned.getQuantity(),
                0
        ));

        if (owned.isEquipped()) {
            findFallback(definition, lookup).ifPresent(fallback -> {
                fallback.markEquipped();
                movements.add(new InventoryMovement(
                        fallback.getDefinitionId(),
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

    private Optional<UserItem> findItem(UUID definitionId) {
        return items.stream()
                .filter(item -> item.getDefinitionId().equals(definitionId))
                .findFirst();
    }

    private Optional<UserItem> findFallback(ItemDefinition revoked, ItemDefinitionLookup lookup) {
        return items.stream()
                .filter(candidate -> {
                    ItemDefinition candidateDefinition = lookup.getById(candidate.getDefinitionId());

                    return candidateDefinition.getKind() == revoked.getKind()
                            && candidateDefinition.getCategory() == revoked.getCategory()
                            && sharesContext(candidateDefinition, revoked);
                })
                .findFirst();
    }

    private boolean sharesContext(ItemDefinition candidate, ItemDefinition revoked) {
        return revoked.getApplicability().stream().anyMatch(candidate::isApplicableTo);
    }
}
