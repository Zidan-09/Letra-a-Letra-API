package com.letraaletra.api.features.inventory.application.output;

import com.letraaletra.api.features.inventory.domain.ItemDefinition;

public record CreateItemDefinitionOutput(
        ItemDefinition definition
) {
}
