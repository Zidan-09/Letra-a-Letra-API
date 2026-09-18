package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;

import java.util.EnumMap;
import java.util.Map;

public class ItemPolicyRegistry {
    private final Map<ItemKind, GrantPolicy> grantPolicies;
    private final Map<ItemKind, ConsumePolicy> consumePolicies;
    private final Map<ItemKind, EquipPolicy> equipPolicies;

    public ItemPolicyRegistry(
            Map<ItemKind, GrantPolicy> grantPolicies,
            Map<ItemKind, ConsumePolicy> consumePolicies,
            Map<ItemKind, EquipPolicy> equipPolicies
    ) {
        this.grantPolicies = new EnumMap<>(grantPolicies);
        this.consumePolicies = new EnumMap<>(consumePolicies);
        this.equipPolicies = new EnumMap<>(equipPolicies);
    }

    public static ItemPolicyRegistry defaults() {
        return new ItemPolicyRegistry(
                Map.of(
                        ItemKind.EQUIPPABLE, new UniqueGrantPolicy(),
                        ItemKind.CONSUMABLE, new StackableGrantPolicy()
                ),
                Map.of(
                        ItemKind.EQUIPPABLE, new ConsumableConsumePolicy(),
                        ItemKind.CONSUMABLE, new ConsumableConsumePolicy()
                ),
                Map.of(
                        ItemKind.EQUIPPABLE, new CosmeticEquipPolicy(),
                        ItemKind.CONSUMABLE, new CosmeticEquipPolicy()
                )
        );
    }

    public GrantPolicy grantPolicyFor(Item item) {
        GrantPolicy policy = grantPolicies.get(kindOf(item));

        if (policy == null) {
            throw new InvalidItemException();
        }

        return policy;
    }

    public ConsumePolicy consumePolicyFor(Item item) {
        ConsumePolicy policy = consumePolicies.get(kindOf(item));

        if (policy == null) {
            throw new InvalidItemException();
        }

        return policy;
    }

    public EquipPolicy equipPolicyFor(Item item) {
        EquipPolicy policy = equipPolicies.get(kindOf(item));

        if (policy == null) {
            throw new InvalidItemException();
        }

        return policy;
    }

    private static ItemKind kindOf(Item item) {
        return item instanceof ConsumableItem ? ItemKind.CONSUMABLE : ItemKind.EQUIPPABLE;
    }
}
