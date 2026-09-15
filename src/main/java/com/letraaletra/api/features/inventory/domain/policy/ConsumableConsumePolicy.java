package com.letraaletra.api.features.inventory.domain.policy;

import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.exception.InapplicableContextException;
import com.letraaletra.api.features.inventory.domain.exception.InsufficientQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.exception.NonConsumableItemException;

public class ConsumableConsumePolicy implements ConsumePolicy {

    @Override
    public void checkConsume(ItemDefinition definition, UserItem owned, int quantity, ItemContext context) {
        if (!definition.isConsumable()) {
            throw new NonConsumableItemException();
        }

        if (!definition.isApplicableTo(context)) {
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
