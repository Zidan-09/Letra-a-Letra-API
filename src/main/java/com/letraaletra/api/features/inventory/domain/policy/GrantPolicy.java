package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;

public interface GrantPolicy {

    void checkGrant(ItemDefinition definition, UserItem existingOwnership, int quantity);
}
