package com.letraaletra.api.features.items.application.output;

import com.letraaletra.api.features.items.domain.Item;
import org.springframework.data.domain.Page;

public record ListItemsOutput(
        Page<Item> items
) {
}
