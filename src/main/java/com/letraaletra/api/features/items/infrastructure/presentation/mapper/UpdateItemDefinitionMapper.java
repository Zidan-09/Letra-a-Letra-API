package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.ItemAssetUpload;
import com.letraaletra.api.features.items.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.UpdateItemDefinitionRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

public class UpdateItemDefinitionMapper {
    public static UpdateItemDefinitionInput toInput(
            AuthenticatedUser principal,
            UUID itemId,
            UpdateItemDefinitionRequest request,
            MultipartFile asset
    ) {
        return new UpdateItemDefinitionInput(
                principal,
                itemId,
                request.name(),
                request.available(),
                toAsset(asset),
                request.isNewAsset()
        );
    }

    public static ItemDefinitionResponse toResponse(UpdateItemDefinitionOutput output) {
        return ItemDefinitionResponseMapper.toResponse(output.definition());
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
