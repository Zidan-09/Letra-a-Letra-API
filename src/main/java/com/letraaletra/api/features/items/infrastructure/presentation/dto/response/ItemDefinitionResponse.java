package com.letraaletra.api.features.items.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemKind;

import java.util.UUID;

public record ItemDefinitionResponse(
        UUID itemId,
        String name,
        ItemKind kind,
        ItemCategory category,
        ItemContext context,
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
