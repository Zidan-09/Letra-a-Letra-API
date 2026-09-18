package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.application.input.CreateItemInput;
import com.letraaletra.api.features.items.application.input.ItemAssetUpload;
import com.letraaletra.api.features.items.application.output.CreateItemOutput;
import com.letraaletra.api.features.items.domain.effect.ItemEffect;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.request.CreateItemRequest;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

public class CreateItemMapper {
    public static CreateItemInput toInput(
            AuthenticatedUser principal,
            CreateItemRequest request
    ) {
        return new CreateItemInput(
                principal,
                request.name(),
                request.kind(),
                request.category(),
                request.context(),
                toEffect(request),
                toAsset(request.asset())
        );
    }

    public static ItemResponse toResponse(CreateItemOutput output) {
        return ItemResponseMapper.toResponse(output.item());
    }

    private static ItemEffect toEffect(CreateItemRequest request) {
        if (request.effectKind() == null || request.effectKind().isBlank()) {
            return null;
        }

        if ("NICKNAME_CHANGE".equals(request.effectKind())) {
            return new NicknameChangeEffect();
        }

        if ("PERCENTAGE_TIMED".equals(request.effectKind())) {
            return new PercentageTimedEffect(request.effectType(), request.magnitude(), request.durationMinutes());
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
