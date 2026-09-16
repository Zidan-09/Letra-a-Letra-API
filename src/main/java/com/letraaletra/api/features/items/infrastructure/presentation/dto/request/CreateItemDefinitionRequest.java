package com.letraaletra.api.features.items.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemKind;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateItemDefinitionRequest(
        @NotBlank String name,
        @NotNull ItemKind kind,
        @NotNull ItemCategory category,
        @NotNull ItemContext context,
        boolean consumable,
        @Valid ItemEffectRequest effect
) {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind", defaultImpl = PercentageTimedEffectRequest.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PercentageTimedEffectRequest.class, name = "PERCENTAGE_TIMED"),
            @JsonSubTypes.Type(value = NicknameChangeEffectRequest.class, name = "NICKNAME_CHANGE")
    })
    public sealed interface ItemEffectRequest permits PercentageTimedEffectRequest, NicknameChangeEffectRequest {
    }

    public record PercentageTimedEffectRequest(
            @NotNull EffectType type,
            @jakarta.validation.constraints.Min(1) int magnitude,
            @jakarta.validation.constraints.Min(1) int durationMinutes
    ) implements ItemEffectRequest {
    }

    public record NicknameChangeEffectRequest() implements ItemEffectRequest {
    }
}
