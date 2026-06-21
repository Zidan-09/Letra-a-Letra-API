package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.UpdateCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.UpdateCosmeticResponse;

public class UpdateCosmeticMapper {
    public static UpdateCosmeticInput toInput(UpdateCosmeticRequest request) {
        return new UpdateCosmeticInput(
                request.id(),
                request.name(),
                request.asset()
        );
    }

    public static UpdateCosmeticResponse toResponse(UpdateCosmeticOutput output) {
        return new UpdateCosmeticResponse(
                output.cosmetic()
        );
    }
}
