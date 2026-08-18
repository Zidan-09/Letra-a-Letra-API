package com.letraaletra.api.features.game.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

public record GetActiveGamesInput(
        AuthenticatedUser principal,
        int page,
        int size,
        Sort sort
) {
}
