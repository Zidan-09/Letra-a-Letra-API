package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.RegisterCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.RegisterCosmeticResponse;

public class RegisterCosmeticMapper {
    public static RegisterCosmeticInput toInput(RegisterCosmeticRequest request) {
        return new RegisterCosmeticInput(
                request.id(),
                request.name(),
                request.cosmeticType()
        );
    }

    public static RegisterCosmeticResponse toResponse(RegisterCosmeticOutput output) {
        return new RegisterCosmeticResponse(
                output.cosmetic()
        );
    }
}
