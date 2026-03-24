package com.letraaletra.api.dto.response.participant;

import com.letraaletra.api.domain.participant.ParticipantRole;

public record ParticipantDTO(
        String id,
        String nickname,
        String avatar,
        ParticipantRole role
) {
}
