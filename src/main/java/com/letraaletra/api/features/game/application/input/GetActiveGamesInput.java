package com.letraaletra.api.features.game.application.input;

import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetActiveGamesInput(
        UUID auth,
        int page,
        int size,
        Sort sort
) {
}
