package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("DETECT_TRAPS")
public record DetectTrapsActionDTO() implements PlayerActionDTO {
}
