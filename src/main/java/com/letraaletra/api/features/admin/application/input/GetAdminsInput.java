package com.letraaletra.api.features.admin.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

public record GetAdminsInput(
        AuthenticatedUser principal,
        int page,
        int size,
        Sort sort
) {
}
