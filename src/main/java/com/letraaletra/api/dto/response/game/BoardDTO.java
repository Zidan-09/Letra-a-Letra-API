package com.letraaletra.api.dto.response.game;

public record BoardDTO(
        boolean revealed,
        Character letter,
        String revealedBy
) {
}
