package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.MaxStackExceededException;

public class StackableGrantPolicy implements GrantPolicy {
    private static final int MAX_STACK = 1000;

    @Override
    public void checkGrant(Item item, UserItem existingOwnership, int quantity) {
        if (quantity < 1) {
            throw new InvalidQuantityException();
        }

        int owned = existingOwnership == null ? 0 : existingOwnership.getQuantity();

        if (owned + quantity > MAX_STACK) {
            throw new MaxStackExceededException();
        }
    }
}
