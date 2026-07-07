package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeCosmeticResponse;

import java.util.UUID;

public class ChangeCosmeticMapper {
    public static ChangeCosmeticInput toInput(String cosmeticId, UUID auth) {
        return new ChangeCosmeticInput(
                auth,
                UUID.fromString(cosmeticId)
        );
    }

    public static ChangeCosmeticResponse toResponse(ChangeCosmeticOutput output) {
        return new ChangeCosmeticResponse(
                output.user().getInventory().getItems()
        );
    }
}
