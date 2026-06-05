package com.letraaletra.api.features.player.infrastructure.presentation.mapper;

import com.letraaletra.api.features.power.domain.PowerType;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.InventoryResponse;
import org.springframework.stereotype.Component;

@Component
public class InventoryDTOMapper {
    public InventoryResponse toDTO(String id, PowerType power) {
        return new InventoryResponse(
                id,
                power.name()
        );
    }
}
