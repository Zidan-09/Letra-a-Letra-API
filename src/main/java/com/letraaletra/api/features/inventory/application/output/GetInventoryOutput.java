package com.letraaletra.api.features.inventory.application.output;

import com.letraaletra.api.features.inventory.domain.UserItem;

import java.util.List;

public record GetInventoryOutput(
        List<UserItem> items
) {
}
