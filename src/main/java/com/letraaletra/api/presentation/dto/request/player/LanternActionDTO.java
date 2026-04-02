package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("LANTERN")
public record LanternActionDTO() implements PlayerActionDTO {
}
