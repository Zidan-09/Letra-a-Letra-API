package com.letraaletra.api.presentation.dto.response.game;

public record WordDTO(
        String word,
        boolean found,
        String foundById
) {
}
