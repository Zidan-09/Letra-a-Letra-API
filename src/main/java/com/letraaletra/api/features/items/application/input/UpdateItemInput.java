package com.letraaletra.api.features.items.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public record UpdateItemInput(
        AuthenticatedUser principal,
        UUID itemId,
        String name,
        Boolean available,
        ItemAssetUpload asset,
        boolean isNewAsset
) {
}
