package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.EnableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.EnableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.EnableCosmeticResponse;
import com.letraaletra.api.features.user.domain.User;

import java.util.UUID;

public class EnableCosmeticMapper {
    public static EnableCosmeticInput toInput(User user, String cosmeticId) {
        return new EnableCosmeticInput(
                user,
                UUID.fromString(cosmeticId)
        );
    }

    public static EnableCosmeticResponse toResponse(EnableCosmeticOutput output) {
        return new EnableCosmeticResponse(
                CosmeticMapper.toDto(output.cosmetic())
        );
    }
}
