package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

public record ListItemsInput(
        AuthenticatedUser principal,
        ItemKind kind,
        EquippableCategory category,
        Boolean available,
        int page,
        int size,
        Sort sort
) {
}
