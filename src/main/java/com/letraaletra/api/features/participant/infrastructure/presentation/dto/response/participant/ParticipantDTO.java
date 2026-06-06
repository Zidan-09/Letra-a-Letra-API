package com.letraaletra.api.features.participant.infrastructure.presentation.dto.response.participant;

import com.letraaletra.api.features.participant.domain.ParticipantRole;

public record ParticipantDTO(
        String id,
        String nickname,
        String avatar,
        ParticipantRole role
) {
}
