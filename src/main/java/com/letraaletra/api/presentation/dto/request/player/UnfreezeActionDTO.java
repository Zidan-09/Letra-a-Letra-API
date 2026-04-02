package com.letraaletra.api.presentation.dto.request.player;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("UNFREEZE")
public record UnfreezeActionDTO() implements PlayerActionDTO {
}
