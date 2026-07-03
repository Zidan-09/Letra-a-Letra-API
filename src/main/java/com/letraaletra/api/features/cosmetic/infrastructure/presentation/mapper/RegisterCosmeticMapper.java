package com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper;

import com.letraaletra.api.features.cosmetic.application.input.RegisterCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.RegisterCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.RegisterCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.RegisterCosmeticResponse;
import com.letraaletra.api.features.user.domain.User;

public class RegisterCosmeticMapper {
    public static RegisterCosmeticInput toInput(User user, RegisterCosmeticRequest request) {
        return new RegisterCosmeticInput(
                user,
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
