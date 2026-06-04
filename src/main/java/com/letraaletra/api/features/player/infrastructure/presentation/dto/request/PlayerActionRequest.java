package com.letraaletra.api.features.player.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RevealActionRequest.class, name = "REVEAL"),
        @JsonSubTypes.Type(value = BlockActionRequest.class, name = "BLOCK"),
        @JsonSubTypes.Type(value = UnblockActionRequest.class, name = "UNBLOCK"),
        @JsonSubTypes.Type(value = TrapActionRequest.class, name = "TRAP"),
        @JsonSubTypes.Type(value = DetectTrapsActionRequest.class, name = "DETECT_TRAPS"),
        @JsonSubTypes.Type(value = SpyActionRequest.class, name = "SPY"),
        @JsonSubTypes.Type(value = FreezeActionRequest.class, name = "FREEZE"),
        @JsonSubTypes.Type(value = UnfreezeActionRequest.class, name = "UNFREEZE"),
        @JsonSubTypes.Type(value = BlindActionRequest.class, name = "BLIND"),
        @JsonSubTypes.Type(value = LanternActionRequest.class, name = "LANTERN"),
        @JsonSubTypes.Type(value = ImmunityActionRequest.class, name = "IMMUNITY")
})
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
