package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

public record ListItemDefinitionsInput(
        AuthenticatedUser principal,
        ItemKind kind,
        ItemCategory category,
        Boolean available,
        int page,
        int size,
        Sort sort
) {
}
