package com.letraaletra.api.features.transaction.application.input;

import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.springframework.data.domain.Sort;

public record FindTransactionsByUserUsernameInput(
        AuthenticatedUser principal,
        String username,
        int page,
        int size,
        Sort sort
) {
}
