package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
public sealed interface PlayerActionRequest
    permits
        RevealActionRequest,
        BlockActionRequest,
        UnblockActionRequest,
        TrapActionRequest,
        DetectTrapsActionRequest,
        SpyActionRequest,
        FreezeActionRequest,
        UnfreezeActionRequest,
        BlindActionRequest,
        LanternActionRequest,
        ImmunityActionRequest
{}
