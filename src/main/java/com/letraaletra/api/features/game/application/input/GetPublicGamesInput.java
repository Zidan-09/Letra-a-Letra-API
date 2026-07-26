package com.letraaletra.api.features.game.application.input;

import org.springframework.data.domain.Sort;

public record GetPublicGamesInput(
        int page,
        int size,
        Sort sort
) {
}
