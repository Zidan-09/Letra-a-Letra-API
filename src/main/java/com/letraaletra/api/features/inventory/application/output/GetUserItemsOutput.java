package com.letraaletra.api.features.inventory.application.output;

import java.util.List;

public record GetUserItemsOutput(
        List<UserItemDetails> items
) {
}
