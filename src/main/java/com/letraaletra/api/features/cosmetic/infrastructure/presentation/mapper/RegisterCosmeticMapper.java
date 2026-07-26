package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.RegisterCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.RegisterCosmeticResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class RegisterCosmeticMapper {
    public static RegisterCosmeticInput toInput(AuthenticatedUser principal, RegisterCosmeticRequest request) {
        return new RegisterCosmeticInput(
                principal,
                request.name(),
                request.cosmeticType(),
                request.asset()
        );
    }

    public static RegisterCosmeticResponse toResponse(RegisterCosmeticOutput output) {
        return new RegisterCosmeticResponse(
                output.cosmetic()
        );
    }
}
