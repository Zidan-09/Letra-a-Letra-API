package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;

public interface GrantPolicy {

    void checkGrant(Item item, UserItem existingOwnership, int quantity);
}
