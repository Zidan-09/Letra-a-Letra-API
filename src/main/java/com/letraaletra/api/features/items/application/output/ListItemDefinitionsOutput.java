package com.letraaletra.api.features.items.application.output;

import com.letraaletra.api.features.items.domain.ItemDefinition;
import org.springframework.data.domain.Page;

public record ListItemDefinitionsOutput(
        Page<ItemDefinition> definitions
) {
}
