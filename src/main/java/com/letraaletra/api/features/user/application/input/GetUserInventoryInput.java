package com.letraaletra.api.features.user.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public record GetUserInventoryInput(
        AuthenticatedUser principal,
        UUID userId,
        int page,
        int size,
        Sort sort
) {
}
