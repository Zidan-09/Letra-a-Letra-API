package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ChangeCosmeticRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeCosmeticResponse;

import java.util.UUID;

public class ChangeCosmeticMapper {
    public static ChangeCosmeticInput toInput(ChangeCosmeticRequest request, String userId) {
        return new ChangeCosmeticInput(
                UUID.fromString(userId),
                request.cosmeticId()
        );
    }

    public static ChangeCosmeticResponse toResponse(ChangeCosmeticOutput output) {
        return new ChangeCosmeticResponse(
                output.user().getInventory().getItems()
        );
    }
}
