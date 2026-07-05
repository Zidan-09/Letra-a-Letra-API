package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.DeleteCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DeleteCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DeleteCosmeticResponse;
import com.letraaletra.api.features.user.domain.User;

import java.util.UUID;

public class DeleteCosmeticMapper {
    public static DeleteCosmeticInput toInput(User user, String cosmeticId) {
        return new DeleteCosmeticInput(
                user,
                UUID.fromString(cosmeticId)
        );
    }

    public static DeleteCosmeticResponse toResponse(DeleteCosmeticOutput output) {
        return new DeleteCosmeticResponse(
                CosmeticMapper.toDto(output.cosmetic())
        );
    }
}
