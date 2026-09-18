package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.ItemAssetUpload;
import com.letraaletra.api.features.items.application.input.UpdateItemInput;
import com.letraaletra.api.features.items.application.output.UpdateItemOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.UpdateItemRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

public class UpdateItemMapper {
    public static UpdateItemInput toInput(
            AuthenticatedUser principal,
            UUID itemId,
            UpdateItemRequest request,
            MultipartFile asset
    ) {
        return new UpdateItemInput(
                principal,
                itemId,
                request.name(),
                request.available(),
                toAsset(asset),
                request.isNewAsset()
        );
    }

    public static ItemResponse toResponse(UpdateItemOutput output) {
        return ItemResponseMapper.toResponse(output.item());
    }

    private static ItemAssetUpload toAsset(MultipartFile asset) {
        if (asset == null || asset.isEmpty()) {
            return null;
        }

        try {
            return new ItemAssetUpload(asset.getBytes(), asset.getContentType());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
