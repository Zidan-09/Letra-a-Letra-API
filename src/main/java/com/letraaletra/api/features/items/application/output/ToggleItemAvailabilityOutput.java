package com.letraaletra.api.features.items.application.output;

import com.letraaletra.api.features.items.domain.ItemDefinition;

public record ToggleItemAvailabilityOutput(
        ItemDefinition definition
) {
}
