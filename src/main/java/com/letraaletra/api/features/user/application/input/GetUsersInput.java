package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetUsersInput(
        AuthenticatedUser principal,
        int page,
        int size,
        Sort sort
) {
}
