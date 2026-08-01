package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.game.domain.board.power.PowerType;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.InventoryResponse;

public class InventoryResponseMapper {
    public static InventoryResponse toResponse(String id, PowerType power) {
        return new InventoryResponse(
                id,
                power.name()
        );
    }
}
