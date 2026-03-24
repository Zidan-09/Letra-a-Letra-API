package com.letraaletra.api.dto.response.game;

public record CellView(
        boolean revealed,
        Character letter,
        String revealedById
) {}