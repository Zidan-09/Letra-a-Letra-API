package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.UpdateCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.UpdateCosmeticResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class UpdateCosmeticMapper {
    public static UpdateCosmeticInput toInput(AuthenticatedUser principal, UpdateCosmeticRequest request, String cosmeticId) {
        return new UpdateCosmeticInput(
                principal,
                UUID.fromString(cosmeticId),
                request.name(),
                request.type(),
                request.asset(),
                request.isNewAsset()
        );
    }

    public static UpdateCosmeticResponse toResponse(UpdateCosmeticOutput output) {
        return new UpdateCosmeticResponse(
                output.cosmetic()
        );
    }
}
