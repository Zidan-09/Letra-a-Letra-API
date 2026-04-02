package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("IMMUNITY")
public record ImmunityActionDTO() implements PlayerActionDTO {
}
