package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.input.ItemAssetUpload;
import com.letraaletra.api.features.inventory.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.domain.ItemEffect;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.request.CreateItemDefinitionRequest;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.ItemDefinitionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

public class CreateItemDefinitionMapper {
    public static CreateItemDefinitionInput toInput(
            AuthenticatedUser principal,
            CreateItemDefinitionRequest request,
            MultipartFile asset
    ) {
        return new CreateItemDefinitionInput(
                principal,
                request.name(),
                request.kind(),
                request.category(),
                request.applicability(),
                request.stackable(),
                request.maxStack(),
                request.consumable(),
                toEffect(request.effect()),
                toAsset(asset)
        );
    }

    public static ItemDefinitionResponse toResponse(CreateItemDefinitionOutput output) {
        return ItemDefinitionResponseMapper.toResponse(output.definition());
    }

    private static ItemEffect toEffect(CreateItemDefinitionRequest.ItemEffectRequest request) {
        if (request == null) {
            return null;
        }

        return new ItemEffect(request.type(), request.magnitude(), request.durationMinutes());
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
