package com.letraaletra.api.dto.response.player;

import java.util.List;

public record PlayerDTO(
        String id,
        String nickname,
        String avatar,
        int score,
        List<String> inventory
) {
}
