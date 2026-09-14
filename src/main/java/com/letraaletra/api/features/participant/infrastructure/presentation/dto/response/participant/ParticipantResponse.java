package com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant;

import com.letraaletra.api.features.participant.domain.EquippedCosmetic;
import com.letraaletra.api.features.participant.domain.ParticipantRole;

import java.util.List;

public record ParticipantResponse(
        String id,
        String nickname,
        List<EquippedCosmetic> cosmeticsEquipped,
        ParticipantRole role,
        boolean isConnected
) {
}
