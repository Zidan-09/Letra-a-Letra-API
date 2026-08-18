package com.letraaletra.api.features.game.domain;

import org.springframework.data.domain.Sort;

public record GamesPage(
        int page,
        int size,
        Sort sort
) {
}
