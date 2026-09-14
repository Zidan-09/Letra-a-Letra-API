package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.MaxStackExceededException;

public class StackableGrantPolicy implements GrantPolicy {

    @Override
    public void checkGrant(ItemDefinition definition, UserItem existingOwnership, int quantity) {
        if (quantity < 1) {
            throw new InvalidQuantityException();
        }

        if (definition.getMaxStack() == null) {
            return;
        }

        int owned = existingOwnership == null ? 0 : existingOwnership.getQuantity();

        if (owned + quantity > definition.getMaxStack()) {
            throw new MaxStackExceededException();
        }
    }
}
