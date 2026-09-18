package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.DuplicateUniqueItemException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;

public class UniqueGrantPolicy implements GrantPolicy {

    @Override
    public void checkGrant(Item item, UserItem existingOwnership, int quantity) {
        if (quantity != 1) {
            throw new InvalidQuantityException();
        }

        if (existingOwnership != null) {
            throw new DuplicateUniqueItemException();
        }
    }
}
