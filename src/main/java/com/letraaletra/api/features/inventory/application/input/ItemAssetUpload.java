package com.letraaletra.api.features.inventory.application.input;

public record ItemAssetUpload(
        byte[] content,
        String contentType
) {
}
