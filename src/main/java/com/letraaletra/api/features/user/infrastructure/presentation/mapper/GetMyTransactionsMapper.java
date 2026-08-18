package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.infrastructure.presentation.dto.response.transaction.TransactionResponse;
import com.letraaletra.api.features.transaction.infrastructure.presentation.mapper.TransactionResponseMapper;
import com.letraaletra.api.features.user.application.input.GetMyTransactionsInput;
import com.letraaletra.api.features.user.application.output.GetMyTransactionsOutput;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class GetMyTransactionsMapper {
    public static GetMyTransactionsInput toInput(AuthenticatedUser principal, Pageable pageable) {
        Pageable pages = pageable == null ?
                PageRequest.of(0, 20, Sort.Direction.ASC) :
                pageable;

        return new GetMyTransactionsInput(
                principal.auth(),
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<TransactionResponse> toResponse(GetMyTransactionsOutput output) {
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
