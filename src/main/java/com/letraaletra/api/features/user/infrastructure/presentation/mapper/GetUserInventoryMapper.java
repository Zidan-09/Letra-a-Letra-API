package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.GetUserInventoryInput;
import com.letraaletra.api.features.user.application.output.GetUserInventoryOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetUserInventoryResponse;

public class GetUserInventoryMapper {
    public static GetUserInventoryInput toInput(String userId) {
        return new GetUserInventoryInput(
                userId
        );
    }

    public static GetUserInventoryResponse toResponse(GetUserInventoryOutput output) {
        return new GetUserInventoryResponse(
                output.inventory()
        );
    }
}
