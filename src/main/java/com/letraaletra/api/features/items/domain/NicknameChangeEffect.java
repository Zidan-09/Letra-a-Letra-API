package com.letraaletra.api.features.items.domain;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("NICKNAME_CHANGE")
public record NicknameChangeEffect() implements ItemEffect {
}
