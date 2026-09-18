package com.letraaletra.api.features.items.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.items.domain.EquippableCategory;
import com.letraaletra.api.features.items.domain.EquippableContext;
import com.letraaletra.api.features.items.domain.ItemKind;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateItemRequest(
        @NotBlank String name,
        @NotNull ItemKind kind,
        EquippableCategory category,
        EquippableContext context,
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
            @Min(1) int magnitude,
            @Min(1) int durationMinutes
    ) implements ItemEffectRequest {
    }

    public record NicknameChangeEffectRequest() implements ItemEffectRequest {
    }
}
