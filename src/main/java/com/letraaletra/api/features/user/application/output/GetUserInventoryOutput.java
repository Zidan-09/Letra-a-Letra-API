package com.letraaletra.api.features.user.application.output;

import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import org.springframework.data.domain.Page;

public record GetUserInventoryOutput(
        Page<InventoryItem> inventory
) {
}
