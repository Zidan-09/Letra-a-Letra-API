package com.letraaletra.api.features.inventory.application.output;

import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.UserItem;

public record UserItemDetails(
        UserItem item,
        ItemDefinition definition
) {
}
