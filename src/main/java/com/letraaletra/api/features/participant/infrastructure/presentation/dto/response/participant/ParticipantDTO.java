package com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant;

import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;

import java.util.List;

public record ParticipantDTO(
        String id,
        String nickname,
        List<InventoryItem> cosmeticsEquipped,
        ParticipantRole role
) {
}
