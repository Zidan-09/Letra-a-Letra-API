package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetUserInventoryResponse;

import java.util.UUID;

public class GetUserInventoryMapper {
    public static GetUserInventoryInput toInput(UUID auth) {
        return new GetUserInventoryInput(
                auth
        );
    }

    public static GetUserInventoryResponse toResponse(GetUserInventoryOutput output) {
        return new GetUserInventoryResponse(
                output.inventory()
        );
    }
}
