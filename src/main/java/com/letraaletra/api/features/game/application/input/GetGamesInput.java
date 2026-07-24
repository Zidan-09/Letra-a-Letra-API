package com.letraaletra.api.features.game.application.input;

import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetGamesInput(
        UUID auth,
        int page,
        int size,
        Sort sort
) {
}
