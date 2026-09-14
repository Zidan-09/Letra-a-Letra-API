package com.letraaletra.api.features.inventory.infrastructure.presentation.mapper;

import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.infrastructure.presentation.dto.response.ItemDefinitionResponse;

public class ItemDefinitionResponseMapper {
    public static ItemDefinitionResponse toResponse(ItemDefinition definition) {
        return new ItemDefinitionResponse(
                definition.getId(),
                definition.getName(),
                definition.getKind(),
                definition.getCategory(),
                definition.getApplicability(),
                definition.isStackable(),
                definition.getMaxStack(),
                definition.isConsumable(),
                toEffectResponse(definition),
                definition.getAssetPath(),
                definition.getVersion(),
                definition.isAvailable()
        );
    }

    private static ItemDefinitionResponse.ItemEffectResponse toEffectResponse(ItemDefinition definition) {
        if (definition.getEffect() == null) {
            return null;
        }

        return new ItemDefinitionResponse.ItemEffectResponse(
                definition.getEffect().type(),
                definition.getEffect().magnitude(),
                definition.getEffect().durationMinutes()
        );
    }
}
