package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DisableCosmeticResponse;

import java.util.UUID;

public class DisableCosmeticMapper {
    public static DisableCosmeticInput toInput(String cosmeticId) {
        return new DisableCosmeticInput(
                UUID.fromString(cosmeticId)
        );
    }

    public static DisableCosmeticResponse toResponse(DisableCosmeticOutput output) {
        return new DisableCosmeticResponse(
                output.cosmetic().getId().toString()
        );
    }
}
