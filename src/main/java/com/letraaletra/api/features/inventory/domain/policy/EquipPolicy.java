package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;

public interface EquipPolicy {

    void checkEquip(Item item, UserItem owned, EquippableContext context);
}
