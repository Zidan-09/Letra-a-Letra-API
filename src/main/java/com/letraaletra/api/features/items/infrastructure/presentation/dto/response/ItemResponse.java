package com.letraaletra.api.features.items.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.letraaletra.api.features.items.domain.item.ItemKind;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.EffectType;

import java.util.UUID;

public record ItemResponse(
        UUID itemId,
        String name,
        ItemKind kind,
        EquippableCategory category,
        EquippableContext context,
        boolean stackable,
        Integer maxStack,
        boolean consumable,
        ItemEffectResponse effect,
        String assetPath,
        int version,
        boolean available
) {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PercentageTimedEffectResponse.class, name = "PERCENTAGE_TIMED"),
            @JsonSubTypes.Type(value = NicknameChangeEffectResponse.class, name = "NICKNAME_CHANGE")
    })
    public sealed interface ItemEffectResponse permits PercentageTimedEffectResponse, NicknameChangeEffectResponse {
    }

    public record PercentageTimedEffectResponse(
            EffectType type,
            int magnitude,
            int durationMinutes
    ) implements ItemEffectResponse {
    }

    public record NicknameChangeEffectResponse() implements ItemEffectResponse {
    }
}
