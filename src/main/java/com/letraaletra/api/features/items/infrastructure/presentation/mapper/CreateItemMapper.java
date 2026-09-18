package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.CreateItemInput;
import com.letraaletra.api.features.items.application.input.ItemAssetUpload;
import com.letraaletra.api.features.items.application.output.CreateItemOutput;
import com.letraaletra.api.features.items.domain.ItemEffect;
import com.letraaletra.api.features.items.domain.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.PercentageTimedEffect;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.CreateItemRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

public class CreateItemMapper {
    public static CreateItemInput toInput(
            AuthenticatedUser principal,
            CreateItemRequest request,
            MultipartFile asset
    ) {
        return new CreateItemInput(
                principal,
                request.name(),
                request.kind(),
                request.category(),
                request.context(),
                toEffect(request.effect()),
                toAsset(asset)
        );
    }

    public static ItemResponse toResponse(CreateItemOutput output) {
        return ItemResponseMapper.toResponse(output.item());
    }

    private static ItemEffect toEffect(CreateItemRequest.ItemEffectRequest request) {
        if (request == null) {
            return null;
        }

        if (request instanceof CreateItemRequest.NicknameChangeEffectRequest) {
            return new NicknameChangeEffect();
        }

        if (request instanceof CreateItemRequest.PercentageTimedEffectRequest timed) {
            return new PercentageTimedEffect(timed.type(), timed.magnitude(), timed.durationMinutes());
        }

        return null;
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
