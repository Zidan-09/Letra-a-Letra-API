package com.letraaletra.api.features.items.infrastructure.presentation.mapper;

import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.PercentageTimedEffect;
import com.letraaletra.api.features.items.infrastructure.presentation.dto.response.ItemDefinitionResponse;

public class ItemDefinitionResponseMapper {
    public static ItemDefinitionResponse toResponse(ItemDefinition definition) {
        return new ItemDefinitionResponse(
                definition.getId(),
                definition.getName(),
                definition.getKind(),
                definition.getCategory(),
                definition.getContext(),
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

        if (definition.getEffect() instanceof NicknameChangeEffect) {
            return new ItemDefinitionResponse.NicknameChangeEffectResponse();
        }

        if (definition.getEffect() instanceof PercentageTimedEffect timed) {
            return new ItemDefinitionResponse.PercentageTimedEffectResponse(
                    timed.type(),
                    timed.magnitude(),
                    timed.durationMinutes()
            );
        }

        return null;
    }
}
