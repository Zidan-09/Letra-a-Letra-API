package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.GetMyInventoryInput;
import com.letraaletra.api.features.user.application.output.GetMyInventoryOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.GetMyInventoryResponse;

import java.util.UUID;

public class GetMyInventoryMapper {
    public static GetMyInventoryInput toInput(UUID auth) {
        return new GetMyInventoryInput(
                auth
        );
    }

    public static GetMyInventoryResponse toResponse(GetMyInventoryOutput output) {
        return new GetMyInventoryResponse(
                output.inventory().stream()
                        .map(InventoryItemResponseMapper::toResponse)
                        .toList()
        );
    }
}
