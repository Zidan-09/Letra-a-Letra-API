package com.letraaletra.api.presentation.dto.request.websocket.playeractions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RevealActionDTO.class, name = "REVEAL"),
        @JsonSubTypes.Type(value = BlockActionDTO.class, name = "BLOCK"),
        @JsonSubTypes.Type(value = UnblockActionDTO.class, name = "UNBLOCK"),
        @JsonSubTypes.Type(value = TrapActionDTO.class, name = "TRAP"),
        @JsonSubTypes.Type(value = DetectTrapsActionDTO.class, name = "DETECT_TRAPS"),
        @JsonSubTypes.Type(value = SpyActionDTO.class, name = "SPY"),
        @JsonSubTypes.Type(value = FreezeActionDTO.class, name = "FREEZE"),
        @JsonSubTypes.Type(value = UnfreezeActionDTO.class, name = "UNFREEZE"),
        @JsonSubTypes.Type(value = BlindActionDTO.class, name = "BLIND"),
        @JsonSubTypes.Type(value = LanternActionDTO.class, name = "LANTERN"),
        @JsonSubTypes.Type(value = ImmunityActionDTO.class, name = "IMMUNITY"),
})
public sealed interface PlayerActionDTO
    permits
        RevealActionDTO,
        BlockActionDTO,
        UnblockActionDTO,
        TrapActionDTO,
        DetectTrapsActionDTO,
        SpyActionDTO,
        FreezeActionDTO,
        UnfreezeActionDTO,
        BlindActionDTO,
        LanternActionDTO,
        ImmunityActionDTO
{}
