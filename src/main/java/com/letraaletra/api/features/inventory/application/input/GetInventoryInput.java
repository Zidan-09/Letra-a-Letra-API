package com.letraaletra.api.features.inventory.application.input;

import java.util.UUID;

public record GetInventoryInput(
        UUID userId
) {
}
