package com.letraaletra.api.features.user.domain;

import org.springframework.data.domain.Sort;

public record UsersPage(
        int page,
        int size,
        Sort sort
) {
}
