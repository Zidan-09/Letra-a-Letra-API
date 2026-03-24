package com.letraaletra.api.dto.request.websocket.playeractions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RevealAction.class, name = "REVEAL"),
        @JsonSubTypes.Type(value = BlockAction.class, name = "BLOCK"),
        @JsonSubTypes.Type(value = UnblockAction.class, name = "UNBLOCK"),
        @JsonSubTypes.Type(value = TrapAction.class, name = "TRAP"),
        @JsonSubTypes.Type(value = DetectTrapsAction.class, name = "DETECT_TRAPS"),
        @JsonSubTypes.Type(value = SpyAction.class, name = "SPY"),
        @JsonSubTypes.Type(value = FreezeAction.class, name = "FREEZE"),
        @JsonSubTypes.Type(value = UnfreezeAction.class, name = "UNFREEZE"),
        @JsonSubTypes.Type(value = BlindAction.class, name = "BLIND"),
        @JsonSubTypes.Type(value = LanternAction.class, name = "LANTERN"),
        @JsonSubTypes.Type(value = ImmunityAction.class, name = "IMMUNITY"),
})
public sealed interface PlayerAction
    permits
        RevealAction,
        BlockAction,
        UnblockAction,
        TrapAction,
        DetectTrapsAction,
        SpyAction,
        FreezeAction,
        UnfreezeAction,
        BlindAction,
        LanternAction,
        ImmunityAction
{}
