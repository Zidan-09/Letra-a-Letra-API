package com.letraaletra.api.features.user.application.input;

import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetUsersInput(
        UUID auth,
        int page,
        int size,
        Sort sort
) {
}
