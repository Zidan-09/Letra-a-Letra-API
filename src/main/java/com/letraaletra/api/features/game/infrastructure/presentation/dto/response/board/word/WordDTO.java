package com.letraaletra.api.features.game.infrastructure.presentation.dto.response.board.word;

public record WordDTO(
        String word,
        boolean found,
        String foundById
) {
}
