package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.power.domain.PowerType;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.InventoryResponse;

public class InventoryDTOMapper {
    public static InventoryResponse toDto(String id, PowerType power) {
        return new InventoryResponse(
                id,
                power.name()
        );
    }
}
