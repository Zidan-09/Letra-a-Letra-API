package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;

public interface ConsumePolicy {

    void checkConsume(ItemDefinition definition, UserItem owned, int quantity, ItemContext context);
}
