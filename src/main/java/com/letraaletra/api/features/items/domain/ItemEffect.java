package com.letraaletra.api.features.items.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind", defaultImpl = PercentageTimedEffect.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PercentageTimedEffect.class, name = "PERCENTAGE_TIMED"),
        @JsonSubTypes.Type(value = NicknameChangeEffect.class, name = "NICKNAME_CHANGE")
})
public sealed interface ItemEffect permits PercentageTimedEffect, NicknameChangeEffect {
}
