package com.letraaletra.api.features.transaction.infrastructure.presentation.mapper;

import com.letraaletra.api.features.transaction.application.input.FindTransactionsByUserUsernameInput;
import com.letraaletra.api.features.transaction.application.output.FindTransactionsByUserUsernameOutput;
import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class FindTransactionsByUserUsernameMapper {
    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "amount");

    public static FindTransactionsByUserUsernameInput toInput(AuthenticatedUser principal, String username, Pageable pageable) {
        Pageable pages = Pageables.sanitize(pageable, ALLOWED_SORTS, Sort.unsorted());

        return new FindTransactionsByUserUsernameInput(
                principal,
                username,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<TransactionResponse> toResponse(FindTransactionsByUserUsernameOutput output) {
        Page<TransactionDetails> page = output.transactions();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(TransactionResponseMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
