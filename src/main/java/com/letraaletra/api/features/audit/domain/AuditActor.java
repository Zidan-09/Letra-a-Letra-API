package com.letraaletra.api.features.audit.domain;

import java.util.UUID;

public record AuditActor(
        AuditActorType type,
        UUID id,
        String name
) {
    public static AuditActor system() {
        return new AuditActor(AuditActorType.SYSTEM, null, "SYSTEM");
    }
}
