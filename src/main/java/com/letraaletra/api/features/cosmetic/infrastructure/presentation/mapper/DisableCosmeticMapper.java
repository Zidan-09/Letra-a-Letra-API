package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DisableCosmeticResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class DisableCosmeticMapper {
    public static DisableCosmeticInput toInput(AuthenticatedUser principal, String cosmeticId) {
        return new DisableCosmeticInput(
                principal,
                UUID.fromString(cosmeticId)
        );
    }

    public static DisableCosmeticResponse toResponse(DisableCosmeticOutput output) {
        return new DisableCosmeticResponse(
                CosmeticResponseMapper.toDto(output.cosmetic())
        );
    }
}
