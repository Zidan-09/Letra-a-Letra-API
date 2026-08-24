package com.letraaletra.api.features.user.application.input;

import org.springframework.data.domain.Sort;

import com.letraaletra.api.shared.domain.AuthenticatedUser;

public record GetUsersInput(
        AuthenticatedUser principal,
        int page,
        int size,
        Sort sort
) {
}
