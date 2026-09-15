package com.letraaletra.api.features.items.application.input;

public record ItemAssetUpload(
        byte[] content,
        String contentType
) {
}
