package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.features.items.domain.*;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public record CreateItemInput(
        AuthenticatedUser principal,
        String name,
        ItemKind kind,
        EquippableCategory category,
        EquippableContext context,
        ItemEffect effect,
        ItemAssetUpload asset
) {
}
