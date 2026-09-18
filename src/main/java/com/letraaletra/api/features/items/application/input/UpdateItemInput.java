package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.features.items.domain.*;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record UpdateItemInput(
        AuthenticatedUser principal,
        UUID itemId,
        String name,
        Boolean available,
        EquippableCategory category,
        EquippableContext context,
        ItemEffect effect,
        ItemAssetUpload asset,
        boolean isNewAsset
) {
}
