package com.letraaletra.api.features.user.application.output;

import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.inventory.domain.UserItem;

public record EquippedItem(
        UserItem owned,
        Item item
) {
}
