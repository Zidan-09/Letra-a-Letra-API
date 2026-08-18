package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.EnableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.EnableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.EnableCosmeticResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class EnableCosmeticMapper {
    public static EnableCosmeticInput toInput(AuthenticatedUser principal, UUID cosmeticId) {
        return new EnableCosmeticInput(
                principal,
                cosmeticId
        );
    }

    public static EnableCosmeticResponse toResponse(EnableCosmeticOutput output) {
        return new EnableCosmeticResponse(
                CosmeticResponseMapper.toResponse(output.cosmetic())
        );
    }
}
