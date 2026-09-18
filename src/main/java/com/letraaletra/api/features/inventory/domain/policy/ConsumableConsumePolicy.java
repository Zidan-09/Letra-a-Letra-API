package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.ConsumableItem;
import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.InapplicableContextException;
import com.letraaletra.api.features.inventory.domain.exception.InsufficientQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.exception.NonConsumableItemException;

public class ConsumableConsumePolicy implements ConsumePolicy {

    @Override
    public void checkConsume(Item item, UserItem owned, int quantity, EquippableContext context) {
        if (!(item instanceof ConsumableItem)) {
            throw new NonConsumableItemException();
        }

        if (context != EquippableContext.PROFILE) {
            throw new InapplicableContextException();
        }

        if (owned == null) {
            throw new ItemNotOwnedException();
        }

        if (quantity < 1) {
            throw new InvalidQuantityException();
        }

        if (owned.getQuantity() < quantity) {
            throw new InsufficientQuantityException();
        }
    }
}
