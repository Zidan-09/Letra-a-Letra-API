package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response;

public record GetApplicationStatusResponse(
        long players,
        long online,
        long games
) {
}
