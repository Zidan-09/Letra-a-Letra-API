package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.InapplicableContextException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.exception.NonEquipableItemException;

public class CosmeticEquipPolicy implements EquipPolicy {

    @Override
    public void checkEquip(Item item, UserItem owned, EquippableContext context) {
        if (!(item instanceof EquippableItem equippable)) {
            throw new NonEquipableItemException();
        }

        if (equippable.getContext() != context) {
            throw new InapplicableContextException();
        }

        if (owned == null) {
            throw new ItemNotOwnedException();
        }
    }
}
