package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;

public interface EquipPolicy {

    void checkEquip(ItemDefinition definition, UserItem owned, ItemContext context);
}
