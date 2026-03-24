package com.letraaletra.api.dto.response.player;

public record PlayerDTO(
        String id,
        String nickname,
        String avatar,
        int score,
        int inventoryLength
) {
}
