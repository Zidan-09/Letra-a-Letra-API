package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DeleteCosmeticResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

import java.util.UUID;

public class DeleteCosmeticMapper {
    public static DeleteCosmeticInput toInput(AuthenticatedUser principal, String cosmeticId) {
        return new DeleteCosmeticInput(
                principal,
                UUID.fromString(cosmeticId)
        );
    }

    public static DeleteCosmeticResponse toResponse(DeleteCosmeticOutput output) {
        return new DeleteCosmeticResponse(
                CosmeticResponseMapper.toDto(output.cosmetic())
        );
    }
}
