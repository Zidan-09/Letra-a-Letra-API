package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.InapplicableContextException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.exception.NonEquipableItemException;

public class CosmeticEquipPolicy implements EquipPolicy {

    @Override
    public void checkEquip(ItemDefinition definition, UserItem owned, ItemContext context) {
        if (definition.getKind() != ItemKind.COSMETIC) {
            throw new NonEquipableItemException();
        }

        if (!definition.isApplicableTo(context)) {
            throw new InapplicableContextException();
        }

        if (owned == null) {
            throw new ItemNotOwnedException();
        }
    }
}
