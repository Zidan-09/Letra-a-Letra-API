package com.letraaletra.api.features.inventory.domain.policy;

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
                        ItemKind.COSMETIC, new UniqueGrantPolicy(),
                        ItemKind.CONSUMABLE, new StackableGrantPolicy()
                ),
                Map.of(
                        ItemKind.COSMETIC, new ConsumableConsumePolicy(),
                        ItemKind.CONSUMABLE, new ConsumableConsumePolicy()
                ),
                Map.of(
                        ItemKind.COSMETIC, new CosmeticEquipPolicy(),
                        ItemKind.CONSUMABLE, new CosmeticEquipPolicy()
                )
        );
    }

    public GrantPolicy grantPolicyFor(ItemKind kind) {
        GrantPolicy policy = grantPolicies.get(kind);

        if (policy == null) {
            throw new InvalidItemException();
        }

        return policy;
    }

    public ConsumePolicy consumePolicyFor(ItemKind kind) {
        ConsumePolicy policy = consumePolicies.get(kind);

        if (policy == null) {
            throw new InvalidItemException();
        }

        return policy;
    }

    public EquipPolicy equipPolicyFor(ItemKind kind) {
        EquipPolicy policy = equipPolicies.get(kind);

        if (policy == null) {
            throw new InvalidItemException();
        }

        return policy;
    }
}
